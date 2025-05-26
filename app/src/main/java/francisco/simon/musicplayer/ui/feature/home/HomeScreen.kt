package francisco.simon.musicplayer.ui.feature.home

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import francisco.simon.musicplayer.ui.feature.widgets.ErrorScreen
import francisco.simon.musicplayer.ui.feature.widgets.LoadingScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {

    LaunchedEffect(true) {
        homeViewModel.event.collectLatest { homeEvent ->
            when (homeEvent) {
                is HomeEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, homeEvent.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    val state = homeViewModel.state.collectAsState()

    when (val currentState = state.value) {
        is HomeState.Error -> {
            val errorMessage = currentState.message
            ErrorScreen(
                errorMessage = errorMessage,
                primaryButton = "Retry",
                onPrimaryButtonClicked = {}
            )
        }

        is HomeState.Loading -> {
            LoadingScreen()
        }

        is HomeState.Success -> {
            Text("Simon")
        }
    }
}