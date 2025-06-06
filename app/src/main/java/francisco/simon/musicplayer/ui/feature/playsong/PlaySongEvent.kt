package francisco.simon.musicplayer.ui.feature.playsong

sealed class PlaySongEvent {
    data class ShowErrorMessage(val message: String) : PlaySongEvent()
}