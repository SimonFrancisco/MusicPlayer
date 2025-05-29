package francisco.simon.musicplayer.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingViewModel() : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Normal)
    val state: StateFlow<OnboardingState> = _state

    private val _event = MutableSharedFlow<OnboardingEvent>()
    val event = _event.asSharedFlow()


    fun fetchData() {

    }
}