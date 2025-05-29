package francisco.simon.musicplayer.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import francisco.simon.musicplayer.data.MusifySession
import francisco.simon.musicplayer.data.model.LoginRequest
import francisco.simon.musicplayer.data.network.Resource
import francisco.simon.musicplayer.data.repository.AuthenticationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(private val authenticationRepository: AuthenticationRepository,
                     private val musifySession: MusifySession
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Nothing)
    val state: StateFlow<LoginState> = _state

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()
    private val _isRememberMeActive = MutableStateFlow(false)
    val isRememberMeActive: StateFlow<Boolean> = _isRememberMeActive.asStateFlow()

    fun onRememberMeClicked() {
        _isRememberMeActive.value = !_isRememberMeActive.value
    }

    fun onPasswordVisibilityChanged() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onRetryClicked() {
        viewModelScope.launch {
            _state.value = LoginState.Nothing
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val email = _email.value
                val password = _password.value
                if (email.isEmpty() || password.isEmpty()) {
                    _event.emit(LoginEvent.ShowErrorMessage("Email and password cannot be empty"))
                    _state.value = LoginState.Nothing
                    return@launch
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _event.emit(LoginEvent.ShowErrorMessage("Invalid email format"))
                    _state.value = LoginState.Nothing
                    return@launch
                }
                if (password.length < 6) {
                    _event.emit(LoginEvent.ShowErrorMessage("Password must be at least 6 characters"))
                    _state.value = LoginState.Nothing
                    return@launch
                }
                val request = LoginRequest(email, password)
                when (val response = authenticationRepository.login(request)) {
                    is Resource.Success -> {
                        val loginResponse = response.data
                        loginResponse.token?.let {
                            musifySession.saveToken(it)
                        }
                        loginResponse.user?.let { user ->
                            musifySession.saveUserName(user.name!!)
                        }
                        _state.value = LoginState.Success
                        _event.emit(LoginEvent.NavigateToHome)
                    }

                    is Resource.Error -> {
                        _state.value = LoginState.Error(response.message ?: "An error occurred")
                    }
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            _event.emit(LoginEvent.NavigateToRegister)
        }

    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _event.emit(LoginEvent.ShowErrorMessage("Register clicked"))
        }
    }

}