package francisco.simon.musicplayer.ui.feature.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import francisco.simon.musicplayer.data.MusifySession
import francisco.simon.musicplayer.data.model.RegisterRequest
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
class RegisterViewModel(private val authenticationRepository: AuthenticationRepository,
                        private val musifySession: MusifySession
) :
    ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Nothing)
    val state: StateFlow<RegisterState> = _state

    private val _event = MutableSharedFlow<RegisterEvent>()
    val event = _event.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    private val _isPasswordVisible = MutableStateFlow(false)
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    fun onPasswordVisibilityChanged() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun onNameChanged(name: String) {
        _name.value = name
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onRetryClicked() {
        viewModelScope.launch {
            _state.value = RegisterState.Nothing
        }
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            val name = _name.value
            val email = _email.value
            val password = _password.value
            val confirmPassword = _confirmPassword.value
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                _state.value = RegisterState.Error("Please fill all the fields")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _state.value = RegisterState.Error("Please enter a valid email")
                return@launch
            }
            if (password.length < 6) {
                _state.value = RegisterState.Error("Password must be at least 6 characters")
                return@launch
            }
            if (password != confirmPassword) {
                _state.value = RegisterState.Error("Passwords do not match")
                return@launch
            }
            _state.value = RegisterState.Loading
            try {
                val request = RegisterRequest(name= name, email = email, password =  password)
                when (val response = authenticationRepository.register(request)) {
                    is Resource.Success -> {
                        val loginResponse = response.data
                        loginResponse.token?.let {
                            musifySession.saveToken(it)
                        }
                        loginResponse.user?.let { user ->
                            musifySession.saveUserName(user.name!!)
                        }
                        _state.value = RegisterState.Success
                        _event.emit(RegisterEvent.NavigateToHome)
                    }

                    is Resource.Error -> {
                        _state.value = RegisterState.Error(response.message ?: "An error occurred")
                    }
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _event.emit(RegisterEvent.NavigateToLogin)
        }
    }

}