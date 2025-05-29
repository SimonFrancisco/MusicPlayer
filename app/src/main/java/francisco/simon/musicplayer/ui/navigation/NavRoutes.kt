package francisco.simon.musicplayer.ui.navigation

import kotlinx.serialization.Serializable

interface MusifyNavRoute {

}

@Serializable
object OnboardingRoute : MusifyNavRoute

@Serializable
object LoginRoute : MusifyNavRoute

@Serializable
object RegisterRoute : MusifyNavRoute

@Serializable
object HomeRoute : MusifyNavRoute
