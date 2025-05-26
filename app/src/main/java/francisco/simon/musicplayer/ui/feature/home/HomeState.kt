package francisco.simon.musicplayer.ui.feature.home

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val data: List<String>) : HomeState()
    data class Error(val message: String) : HomeState()
}