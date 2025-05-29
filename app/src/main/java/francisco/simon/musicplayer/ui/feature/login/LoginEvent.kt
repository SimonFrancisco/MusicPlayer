package francisco.simon.musicplayer.ui.feature.login

sealed class LoginEvent {
    data class ShowErrorMessage(val message: String) : LoginEvent()
    data object NavigateToRegister : LoginEvent()
    data object NavigateToHome : LoginEvent()
}