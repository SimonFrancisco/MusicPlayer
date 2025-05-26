package francisco.simon.musicplayer.ui.feature.login

sealed class LoginState {
    data object Loading : LoginState()
    data class Success(val data: List<String>) : LoginState()
    data class Error(val message: String) : LoginState()
}