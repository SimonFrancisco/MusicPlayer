package francisco.simon.musicplayer.ui.feature.playsong

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import francisco.simon.musicplayer.data.model.Song
import francisco.simon.musicplayer.data.network.Resource
import francisco.simon.musicplayer.data.repository.MusicRepository
import francisco.simon.musicplayer.data.service.MusifyPlayBackService
import francisco.simon.musicplayer.data.service.MusifyPlayBackService.Companion.KEY_SONG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PlaySongViewModel(
    private val repo: MusicRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<PlaySongState>(PlaySongState.Loading)
    val state: StateFlow<PlaySongState> = _state

    private val _event = MutableSharedFlow<PlaySongEvent>()
    val event = _event.asSharedFlow()

    private var playbackService: MusifyPlayBackService? = null
    private var isServiceBound = false
    private var currentSong: Song? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            isServiceBound = true
            playbackService = (binder as MusifyPlayBackService.MusicBinder).getService()
            observePlaybackService()
            currentSong?.let {
                playbackService?.playSong(it)
            } ?: kotlin.run {
                _state.value = PlaySongState.Error("No song to play")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            playbackService = null
        }
    }

    private fun observePlaybackService() {
        playbackService?.let { service ->
            service.playerState.onEach {
                _state.value = PlaySongState.Success(
                    isPlaying = it.isPlaying,
                    currentSong = it.currentSong,
                    currentPosition = it.currentPosition.coerceAtLeast(0),
                    duration = it.duration.coerceAtLeast(0),
                    error = it.error,
                    isBuffering = it.isBuffering
                )
            }.launchIn(viewModelScope)

        }
    }
    fun fetchData(songId: String) {
        viewModelScope.launch {
            try {
                val response = repo.getSongById(songId)
                if (response is Resource.Success) {
                    currentSong = response.data
                    startServiceAndBind(response.data)
                } else {
                    _state.value = PlaySongState.Error("Failed to fetch song data")
                }
            } catch (e: Exception) {
                _state.value = PlaySongState.Error("Network error: ${e.message}")
            }
        }
    }

    fun togglePlayPause() {
        playbackService?.let { service ->
            if (service.isPlaying()) {
                service.pauseSong()
            } else {
                service.resumeSong()
            }
        } ?: run {
            _state.value = PlaySongState.Error("Playback service not bound")
        }
    }

    fun seekTo(position:Long){
        playbackService?.mediaSessionCallBack?.onSeekTo(position)?: run {
            _state.value = PlaySongState.Error("Playback service not bound")
        }
    }



    private fun startServiceAndBind(song: Song) {
        val intent = Intent(context, MusifyPlayBackService::class.java).apply {
            action = MusifyPlayBackService.ACTION_PLAY
            putExtra(KEY_SONG, song)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        if (!isServiceBound) {
            context.bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}