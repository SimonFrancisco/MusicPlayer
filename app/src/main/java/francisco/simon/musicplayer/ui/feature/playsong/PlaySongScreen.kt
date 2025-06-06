package francisco.simon.musicplayer.ui.feature.playsong

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import francisco.simon.musicplayer.ui.feature.widgets.ErrorScreen
import francisco.simon.musicplayer.ui.feature.widgets.LoadingScreen
import francisco.simon.musicplayer.ui.feature.widgets.MusifySpacer
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaySongScreen(
    songID: String,
    navController: NavController,
    viewModel: PlaySongViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.fetchData(songID)
        viewModel.event.collectLatest {
            when (it) {
                is PlaySongEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state.value) {
        is PlaySongState.Error -> {
            val errorMessage = currentState.message
            ErrorScreen(
                errorMessage = errorMessage,
                primaryButton = "Retry",
                onPrimaryButtonClicked = {}
            )
        }

        is PlaySongState.Loading -> {
            LoadingScreen()
        }

        is PlaySongState.Success -> {
            currentState.currentSong?.let {
                PlaySongScreenContent(
                    title = currentState.currentSong.title,
                    genre = currentState.currentSong.genre,
                    image = currentState.currentSong.coverImage,
                    duration = currentState.duration,
                    currentPosition = currentState.currentPosition,
                    isPlaying = currentState.isPlaying,
                    isBuffering = currentState.isBuffering,
                    onSeekChange = {
                        viewModel.seekTo(it)
                    },
                    onPlayPauseToggle = {
                        viewModel.togglePlayPause()
                    }
                )
            }
        }
    }
}


@Composable
fun PlaySongScreenContent(
    title: String,
    genre: String,
    image: String,
    duration: Long,
    currentPosition: Long,
    isPlaying: Boolean = false,
    isBuffering: Boolean = false,
    onSeekChange: (newValue: Long) -> Unit,
    onPlayPauseToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NowPlayingContent(
            title = title,
            isPlayList = false
        )
        SongContent(
            title = title,
            genre = genre,
            image = image,
        )
        SongActions(
            duration = duration,
            currentPosition = currentPosition,
            isBuffering = isBuffering,
            isPlaying = isPlaying,
            onSeekChange = onSeekChange,
            onPlayPauseToggle = {
                onPlayPauseToggle.invoke()
            },
            onNextClicked = {

            },
            onPreviousClicked = {

            }
        )
    }

}

@Composable
fun SongContent(
    title: String,
    genre: String,
    image: String,

    ) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        MusifySpacer(16.dp)
        AsyncImage(
            model = image,
            contentDescription = "Song Cover Image",
            modifier = Modifier.size(300.dp)
        )
        MusifySpacer(16.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = genre,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@Composable
fun SongActions(
    duration: Long,
    currentPosition: Long,
    isBuffering: Boolean = false,
    isPlaying: Boolean = false,
    onSeekChange: (Long) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onNextClicked: () -> Unit = {},
    onPreviousClicked: () -> Unit = {}

) {
    MusifySpacer(16.dp)
    if (isBuffering) {
        LinearProgressIndicator()
    }
    Slider(
        value = currentPosition.toFloat(),
        onValueChange = { newValue ->
            onSeekChange(newValue.toLong())
        },
        modifier = Modifier.fillMaxWidth(),
        valueRange = 0f..duration.toFloat(),
        //enabled = !isBuffering,
    )
    Row {
        Text(
            text = formattedTime(currentPosition),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Box(modifier = Modifier.weight(1f))
        Text(
            text = formattedTime(duration),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary

        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClicked) {
            Icon(
                painter = painterResource(
                    id = android.R.drawable.ic_media_previous
                ),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        val ic = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        IconButton(
            onClick = onPlayPauseToggle,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(
                    id = ic
                ),
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.onPrimary

            )
        }
        IconButton(onClick = onNextClicked) {
            Icon(
                painter = painterResource(
                    id = android.R.drawable.ic_media_next
                ),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onPrimary

            )
        }
    }

}

private fun formattedTime(millis: Long): String {
    val duration = millis / 1000
    val seconds = (duration % 60).toInt()
    val minutes = (duration / 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun NowPlayingContent(
    title: String, isPlayList: Boolean = false
) {
    val message = if (isPlayList) {
        "Playing from Playlist"
    } else {
        "Now Playing"
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = message.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.surfaceVariant,

            )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}



