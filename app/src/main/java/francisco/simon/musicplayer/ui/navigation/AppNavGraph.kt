package francisco.simon.musicplayer.ui.navigation

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import francisco.simon.musicplayer.ui.feature.login.LoginScreen
import francisco.simon.musicplayer.ui.feature.onboarding.OnboardingScreen
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
        composable<LoginRoute>{
            LoginScreen(navController)
        }
        composable<RegisterRoute> {
            RegisterScreen(navController)

        }
        composable<HomeRoute> {
            Log.d("AppNavGraph", "HomeScreen is not implemented yet")
            Text(text = "Home Screen", style = MaterialTheme.typography.titleLarge)
            // HomeScreen(navController)
        }
    }
}