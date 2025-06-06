package francisco.simon.musicplayer.ui.feature.home

import francisco.simon.musicplayer.data.model.HomeDataResponse

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val data: HomeDataResponse) : HomeState()
    data class Error(val message: String) : HomeState()
}