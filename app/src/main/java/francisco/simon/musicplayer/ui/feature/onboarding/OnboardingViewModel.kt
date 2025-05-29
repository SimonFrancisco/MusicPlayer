package francisco.simon.musicplayer.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingViewModel() : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Normal)
    val state: StateFlow<OnboardingState> = _state

    private val _event = MutableSharedFlow<OnboardingEvent>()
    val event = _event.asSharedFlow()

    fun fetchData() {

    }

    fun onGetStartedClicked(){
        viewModelScope.launch {
            //todo: update the storage to no show the onboarding again.
            _event.emit(OnboardingEvent.NavigateToLogin)
        }
    }

}