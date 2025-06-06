package francisco.simon.musicplayer.data.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.net.toUri
import androidx.media.session.MediaButtonReceiver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import francisco.simon.musicplayer.data.helper.MusifyNotificationHelper
import francisco.simon.musicplayer.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MusifyPlayBackService : Service() {
    companion object {
        const val ACTION_PLAY = "francisco.simon.musify_android.ACTION_PLAY"
        const val ACTION_PAUSE = "francisco.simon.musify_android.ACTION_PAUSE"
        const val ACTION_STOP = "francisco.simon.musify_android.ACTION_STOP"
        const val ACTION_PREVIOUS = "francisco.simon.musify_android.ACTION_PREVIOUS"
        const val ACTION_NEXT = "francisco.simon.musify_android.ACTION_NEXT"
        const val ACTION_PREPARE_SONG = "francisco.simon.musify_android.ACTION_PREPARE_SONG"
        const val KEY_SONG = "SONG"
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusifyPlayBackService {
            return this@MusifyPlayBackService
        }
    }

    private val binder = MusicBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private val notificationHelper: MusifyNotificationHelper by inject()

    private var positionUpdateJob: Job? = null
    private var notificationUpdateJob: Job? = null


    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    _playerState.value = playerState.value.copy(
                        isBuffering = true,
                        currentPosition = exoPlayer.currentPosition,
                        duration = exoPlayer.duration,
                        error = null,
                        isPlaying = false
                    )
                    updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING)
                    updateMediaSessionState()
                }

                Player.STATE_READY -> {
                    _playerState.value = playerState.value.copy(
                        isPlaying = exoPlayer.isPlaying,
                        currentPosition = exoPlayer.currentPosition,
                        duration = exoPlayer.duration,
                        error = null,
                        isBuffering = false
                    )
                    if (exoPlayer.isPlaying) {
                        startForegroundServiceIfNeeded()
                        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    } else {
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
                    }
                    updateMediaSessionState()


                }

                Player.STATE_ENDED -> {
                    _playerState.value = playerState.value.copy(
                        isPlaying = false,
                        currentPosition = 0L,
                        duration = 0L,
                        error = null,
                        isBuffering = false
                    )
                    updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
                    updateMediaSessionState()
                }

                Player.STATE_IDLE -> {
                    _playerState.value = playerState.value.copy(
                        isPlaying = false,
                        currentPosition = 0L,
                        duration = 0L,
                        error = null,
                        isBuffering = false
                    )
                    updatePlaybackState(PlaybackStateCompat.STATE_NONE)
                    updateMediaSessionState()
                }


            }
        }
    }

    private fun updatePlaybackState(state: Int) {
        val position = exoPlayer.currentPosition
        val newState = PlaybackStateCompat.Builder()
            .setState(state, position, 1f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP or
                        PlaybackStateCompat.ACTION_SEEK_TO or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

            ).build()
        mediaSession.setPlaybackState(newState)
    }

    private fun updateMediaSessionState() {
        if (exoPlayer.isPlaying || _playerState.value.currentSong != null) {
            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }
        } else {
            if (mediaSession.isActive) {
                mediaSession.isActive = false
            }
        }
    }

     val mediaSessionCallBack = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            resumeSong()
        }

        override fun onPause() {
            pauseSong()
        }

        override fun onStop() {
            super.onStop()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            exoPlayer.seekTo(pos)
            _playerState.value = playerState.value.copy(
                currentPosition = pos,
                duration = exoPlayer.duration,
                isBuffering = exoPlayer.isLoading,
                isPlaying = exoPlayer.isPlaying,
                error = null
            )
        }

    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MusifyPlayBackService",this.toString())
        exoPlayer = ExoPlayer.Builder(this).build().also {
            it.playWhenReady = true
            it.addListener(playerListener)
        }

        mediaSession = MediaSessionCompat(this, "MusifyPlayBackService").also {
            it.isActive = true
            it.setCallback(mediaSessionCallBack)
            it.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                                PlaybackStateCompat.ACTION_PAUSE or
                                PlaybackStateCompat.ACTION_STOP or
                                PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

                    ).build()
            )
        }
        startPositionUpdate()

    }

    private fun startPositionUpdate() {
        positionUpdateJob?.cancel()
        positionUpdateJob = serviceScope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playerState.value = playerState.value.copy(
                        currentPosition = exoPlayer.currentPosition,
                        duration = exoPlayer.duration,
                        isBuffering = exoPlayer.isLoading,
                        isPlaying = exoPlayer.isPlaying,
                        error = null
                    )
                }
                delay(500)
            }
        }
    }


    private var isForegroundService = false
    private var currentNotification: Notification? = null
    fun startForegroundServiceIfNeeded() {
        val currentSong = playerState.value.currentSong ?: return
        if (!isForegroundService) {
            notificationHelper.createPlayerNotification(
                isPlaying = playerState.value.isPlaying,
                song = currentSong,
                mediaSession = mediaSession
            ) {
                if (!isForegroundService) {
                    try {
                        currentNotification = it
                        startForeground(
                            MusifyNotificationHelper.NOTIFICATION_ID,
                            it
                        )
                        isForegroundService = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    currentNotification = it
                    updateNotification()
                }

            }
        } else {
            updateNotification()
        }
    }

    private fun updateNotification() {
        notificationUpdateJob?.cancel()
        notificationUpdateJob = serviceScope.launch {
            notificationHelper.createPlayerNotification(
                playerState.value.isPlaying,
                playerState.value.currentSong ?: return@launch,
                mediaSession
            ) {
                try {
                    currentNotification = it
                    notificationHelper.updateNotification(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopForegroundService() {
        if (isForegroundService) {
            try {
                mediaSession.isActive = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                isForegroundService = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
        }
        Log.d("MusifyPlayBackService", intent?.action.toString())
        Log.d("MusifyPlayBackService", this.toString())
        when (intent?.action) {
            ACTION_PLAY -> {
                val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(KEY_SONG, Song::class.java)
                } else {
                    intent.getParcelableExtra(KEY_SONG)
                }
                if (song != null) {
                    playSong(song)
                } else {
                    if (_playerState.value.currentSong != null){
                        resumeSong()
                    }
                }
            }

            ACTION_PAUSE -> {
                pauseSong()
            }

            ACTION_STOP -> {

            }

            ACTION_PREVIOUS -> {

            }

            ACTION_NEXT -> {

            }

        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun playSong(song: Song) {
        try {
            _playerState.value = playerState.value.copy(
                currentSong = song,
                isBuffering = true,
                currentPosition = 0L,
                duration = song.duration.toLong()
            )

            val metaBuilder = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist.name)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.coverImage)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.coverImage)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id)

            mediaSession.setMetadata(metaBuilder.build())
            val mediaItem = MediaItem.fromUri(song.audioUrl.toUri())
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } catch (e: Exception) {
            _playerState.value = playerState.value.copy(
                currentSong = null,
                isBuffering = false,
                error = e.message
            )
            e.printStackTrace()
        }
    }

     fun pauseSong() {
        try {
            exoPlayer.pause()
            _playerState.value = playerState.value.copy(
                currentPosition = exoPlayer.currentPosition,
                isPlaying = false,
                duration = exoPlayer.duration
            )

        } catch (e: Exception) {
            _playerState.value = playerState.value.copy(
                currentSong = null,
                isBuffering = false,
                error = e.message
            )
            e.printStackTrace()
        }
        updateNotification()
    }
    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    fun resumeSong() {
        try {
            exoPlayer.play()
            _playerState.value = playerState.value.copy(
                currentPosition = exoPlayer.currentPosition,
                isPlaying = true,
                duration = exoPlayer.duration
            )
            startForegroundServiceIfNeeded()
        } catch (e: Exception) {
            _playerState.value = playerState.value.copy(
                currentSong = null,
                isBuffering = false,
                error = e.message
            )
            e.printStackTrace()
        }
        updateNotification()
    }
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null,
    val isBuffering: Boolean = false
)