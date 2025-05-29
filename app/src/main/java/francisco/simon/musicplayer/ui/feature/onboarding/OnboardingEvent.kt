package francisco.simon.musicplayer.ui.feature.onboarding

sealed class OnboardingEvent {
    data class ShowErrorMessage(val message: String) : OnboardingEvent()
}