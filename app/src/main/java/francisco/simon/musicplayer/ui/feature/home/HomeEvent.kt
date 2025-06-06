package francisco.simon.musicplayer.ui.feature.home

sealed class HomeEvent {
    data class ShowErrorMessage(val message: String) : HomeEvent()
    data class OnSongClick(val songId:String):HomeEvent()
}