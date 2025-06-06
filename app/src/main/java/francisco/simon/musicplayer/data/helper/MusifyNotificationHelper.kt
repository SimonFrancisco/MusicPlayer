package francisco.simon.musicplayer.data.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import francisco.simon.musicplayer.MainActivity
import francisco.simon.musicplayer.R
import francisco.simon.musicplayer.data.model.Song
import francisco.simon.musicplayer.data.service.MusifyPlayBackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.net.URL

@Single
class MusifyNotificationHelper(
    private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "musify_notification_channel"
        private const val CHANNEL_NAME = "Musify Notification Channel"
        private const val CHANNEL_DESCRIPTION = "Channel for Musify playback notifications"
        const val NOTIFICATION_ID = 1

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    setSound(null, null)
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun createPlayerNotification(
        isPlaying: Boolean,
        song: Song,
        mediaSession: MediaSessionCompat, callBack: (Notification) -> Unit
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist.name)
            .setSmallIcon(R.drawable.ic_profile)
            .setContentIntent(pendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        val previousIntent = Intent(context, MusifyPlayBackService::class.java).apply {
            action = MusifyPlayBackService.ACTION_PREVIOUS
        }
        val nextIntent = Intent(context, MusifyPlayBackService::class.java).apply {
            action = MusifyPlayBackService.ACTION_NEXT
        }
        val playIntent = Intent(context, MusifyPlayBackService::class.java).apply {
            action = MusifyPlayBackService.ACTION_PLAY
        }
        val pauseIntent = Intent(context, MusifyPlayBackService::class.java).apply {
            action = MusifyPlayBackService.ACTION_PAUSE
        }

        val previousPendingIntent = PendingIntent.getService(
            context,
            1,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextPendingIntent = PendingIntent.getService(
            context,
            2,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPendingIntent = PendingIntent.getService(
            context,
            3,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getService(
            context,
            4,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("createPlayerNotification", isPlaying.toString())
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "Previous",
                previousPendingIntent
            )

        )
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
        )
        if (isPlaying) {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    pausePendingIntent
                )
            )
        } else {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    playPendingIntent
                )
            )
        }
        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        callBack(notification)
        loadAlbumIcon(notificationBuilder, song.coverImage, callBack)

    }

    private fun loadAlbumIcon(
        builder: NotificationCompat.Builder, url: String, callBack: (Notification) -> Unit
    ) {
        try {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    try {
                        val imageUrl = URL(url)
                        val connection = imageUrl.openConnection()
                        connection.connect()
                        val input = connection.getInputStream()
                        val bitmap = BitmapFactory.decodeStream(input)

                        bitmap
                    } catch (e: Exception) {
                        null
                    }
                }
                bitmap?.let {
                    builder.setLargeIcon(it)
                    callBack(builder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateNotification(notification: Notification) {
        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}