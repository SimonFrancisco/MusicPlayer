package francisco.simon.musicplayer.ui.feature.login

sealed class LoginState {
    data object Nothing:LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()

}