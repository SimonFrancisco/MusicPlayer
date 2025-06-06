package francisco.simon.musicplayer.ui.feature.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import francisco.simon.musicplayer.R
import francisco.simon.musicplayer.ui.feature.widgets.ErrorScreen
import francisco.simon.musicplayer.ui.feature.widgets.LoadingScreen
import francisco.simon.musicplayer.ui.feature.widgets.MusifySpacer
import francisco.simon.musicplayer.ui.feature.widgets.MusifyTextField
import francisco.simon.musicplayer.ui.feature.widgets.SocialCard
import francisco.simon.musicplayer.ui.navigation.HomeRoute
import francisco.simon.musicplayer.ui.navigation.LoginRoute
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = koinViewModel()) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword = viewModel.confirmPassword.collectAsStateWithLifecycle()
    val isPasswordVisible = viewModel.isPasswordVisible.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is RegisterEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }

                is RegisterEvent.NavigateToLogin -> {
                    navController.popBackStack()
                }

                RegisterEvent.NavigateToHome -> {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is RegisterState.Loading -> {
            LoadingScreen()
        }

        is RegisterState.Success -> {

        }

        is RegisterState.Error -> {
            val errorMessage = (state.value as RegisterState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {
                viewModel.onRetryClicked()
            })
        }

        is RegisterState.Nothing -> {
            RegisterScreenContent(
                name.value,
                email.value,
                password.value,
                confirmPassword.value,
                isPasswordVisible.value,
                onRegisterClicked = {
                    viewModel.onRegisterClicked()
                },
                onLoginClicked = {
                    viewModel.onLoginClicked()
                },
                onShowPassClicked = {
                    viewModel.onPasswordVisibilityChanged()
                },
                onEmailChange = {
                    viewModel.onEmailChanged(it)
                },
                onPasswordChange = {
                    viewModel.onPasswordChanged(it)
                },
                onNameChange = {
                    viewModel.onNameChanged(it)
                },
                onConfirmPasswordChange = {
                    viewModel.onConfirmPasswordChanged(it)
                }
            )
        }
    }
}

@Composable
fun RegisterScreenContent(
    name: String,
    email: String,
    pass: String,
    confirmPass: String,
    isPasswordVisible: Boolean,
    onRegisterClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onShowPassClicked: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(painter = painterResource(R.drawable.ic_back), contentDescription = null)
        MusifySpacer(size = 16.dp)
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center),
            )
        }
        MusifySpacer(16.dp)
        Text(
            "Register your account", style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        MusifySpacer(16.dp)
        MusifyTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            placeholder = { Text("Enter your name") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.mail),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
        MusifySpacer(8.dp)

        MusifyTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.mail),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
        MusifySpacer(8.dp)
        MusifyTextField(
            value = pass,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.lock),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onShowPassClicked) {
                    Image(
                        painter = painterResource(R.drawable.eye_off),
                        contentDescription = null,
                        colorFilter = if (isPasswordVisible) {
                            androidx.compose.ui.graphics.ColorFilter.tint(
                                MaterialTheme.colorScheme.primary
                            )
                        } else {
                            null
                        }
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) {
                androidx.compose.ui.text.input.VisualTransformation.None
            } else {
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            }
        )
        MusifySpacer(8.dp)
        MusifyTextField(
            value = confirmPass,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm Password") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.lock),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onShowPassClicked) {

                    Image(
                        painter = painterResource(R.drawable.eye_off),
                        contentDescription = null,
                        colorFilter = if (isPasswordVisible) {
                            androidx.compose.ui.graphics.ColorFilter.tint(
                                MaterialTheme.colorScheme.primary
                            )
                        } else {
                            null
                        }
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) {
                androidx.compose.ui.text.input.VisualTransformation.None
            } else {
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            }
        )
        MusifySpacer(16.dp)
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor =
                    MaterialTheme.colorScheme.primary
                ),
            onClick = onRegisterClicked
        ) {
            Text("Register", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        }


        Box(modifier = Modifier.weight(1f))
        SocialCard(R.string.already_have_an_account, onClick = onLoginClicked, onFbClick = {}) { }
    }
}