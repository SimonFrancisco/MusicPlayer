package francisco.simon.musicplayer.ui.feature.onboarding

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import francisco.simon.musicplayer.R
import francisco.simon.musicplayer.ui.feature.widgets.HighlightedText
import francisco.simon.musicplayer.ui.navigation.LoginRoute
import francisco.simon.musicplayer.ui.navigation.OnboardingRoute
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is OnboardingEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
                is OnboardingEvent.NavigateToLogin -> {
                    navController.navigate(LoginRoute){
                        popUpTo(OnboardingRoute) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
    val state = viewModel.state.collectAsState()
    val cardHeight = remember{
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    when (val currentState = state.value) {
        is OnboardingState.Normal -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.welcome),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_girl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                y = (-(cardHeight.value.minus(20.dp)))
                            ),
                        contentScale = ContentScale.Crop
                    )
                    OnBoardingCard(
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .onGloballyPositioned {
                                val heightPx = it.size.height
                                cardHeight.value = with(density){
                                    heightPx.toDp()
                                }
                            },
                        onClick = {
                            viewModel.onGetStartedClicked()
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun OnBoardingCard(modifier: Modifier = Modifier, onClick:()->Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        HighlightedText(
            textResourceId =  R.string.onboarding_text,
            spanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(16.dp),
            textStyle = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .width(70.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
            ) {

            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {

            }
        }
        Button(onClick = {
            onClick()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Get started", style = MaterialTheme.typography.labelLarge)
        }
    }
}