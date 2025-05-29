package francisco.simon.musicplayer

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import francisco.simon.musicplayer.ui.navigation.AppNavGraph
import francisco.simon.musicplayer.ui.navigation.OnboardingRoute
import francisco.simon.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val SPLASH_SCREEN_VISIBILITY = 1000L

class MainActivity : ComponentActivity() {
    private var isSplashScreenVisible = true
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { isSplashScreenVisible }
            setOnExitAnimationListener { splashScreenViewProvider ->
                val zoomX = ObjectAnimator.ofFloat(
                    splashScreenViewProvider.iconView,
                    "scaleX",
                    0.4f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    splashScreenViewProvider.iconView,
                    "scaleY",
                    0.4f,
                    0f
                )
                zoomX.duration = 300
                zoomY.duration = 300
                zoomX.doOnEnd {
                    splashScreenViewProvider.remove()
                }
                zoomY.doOnEnd {
                    splashScreenViewProvider.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavGraph(navController = rememberNavController(), startDestination = OnboardingRoute)
                    }

                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(SPLASH_SCREEN_VISIBILITY)
            isSplashScreenVisible = false
        }
    }
}


