package francisco.simon.musicplayer.ui.feature.register

sealed class RegisterState {
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
    data object Nothing : RegisterState()
}