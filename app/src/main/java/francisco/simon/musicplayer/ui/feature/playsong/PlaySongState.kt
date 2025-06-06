package francisco.simon.musicplayer.ui.feature.playsong

import francisco.simon.musicplayer.data.model.Song

sealed class PlaySongState {
    data object Loading : PlaySongState()
    data class Success(
        val isPlaying: Boolean = false,
        val currentSong: Song? = null,
        val currentPosition: Long = 0L,
        val duration: Long = 0L,
        val error: String? = null,
        val isBuffering: Boolean = false
    ) : PlaySongState()

    data class Error(val message: String) : PlaySongState()
}