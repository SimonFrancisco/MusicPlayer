package francisco.simon.musicplayer.ui.feature.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import francisco.simon.musicplayer.ui.feature.widgets.ErrorScreen
import francisco.simon.musicplayer.ui.feature.widgets.LoadingScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = koinViewModel()) {

    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is LoginEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    val state = viewModel.state.collectAsState()

    when (val currentState = state.value) {
        is LoginState.Error -> {
            val errorMessage = currentState.message
            ErrorScreen(
                errorMessage = errorMessage,
                primaryButton = "Retry",
                onPrimaryButtonClicked = {}
            )
        }

        is LoginState.Loading -> {
            LoadingScreen()
        }

        is LoginState.Success -> {
        }
    }
}