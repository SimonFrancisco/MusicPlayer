package francisco.simon.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import francisco.simon.musicplayer.ui.feature.home.HomeScreen
import francisco.simon.musicplayer.ui.feature.login.LoginScreen
import francisco.simon.musicplayer.ui.feature.onboarding.OnboardingScreen
import francisco.simon.musicplayer.ui.feature.playsong.PlaySongScreen
import francisco.simon.musicplayer.ui.feature.register.RegisterScreen

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: MusifyNavRoute) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                navController
            )
        }
        composable<LoginRoute> {
            LoginScreen(navController)
        }
        composable<RegisterRoute> {
            RegisterScreen(navController)

        }
        composable<HomeRoute> {
            HomeScreen(navController)
        }
        composable<PlaySongRoute> {
            val route = it.toRoute<PlaySongRoute>()
            PlaySongScreen(songID = route.id, navController = navController)
        }
    }
}