package francisco.simon.musicplayer.ui.feature.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import francisco.simon.musicplayer.R

@Composable
fun SocialCard(
    stringRes: Int,
    onClick: () -> Unit,
    onFbClick: () -> Unit,
    onGoogleClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MusifySpacer(16.dp)
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
            MusifySpacer(8.dp)
            Text(
                "or continue with ", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Normal
            )
            MusifySpacer(8.dp)
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
            MusifySpacer(16.dp)
        }
        MusifySpacer(16.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialButtons(onClick = onFbClick, res = R.drawable.fb)
            MusifySpacer(16.dp)
            SocialButtons(onClick = onFbClick, res = R.drawable.google)
        }
        MusifySpacer(16.dp)
        HighlightedText(
            stringRes,
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.clickable { onClick.invoke() },
            textStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
        )
        MusifySpacer(16.dp)
    }
}


@Composable
fun SocialButtons(
    onClick: () -> Unit,
    res: Int,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .size(48.dp)
            .background(Color.Black)
            .clickable {
                onClick.invoke()
            },
    ) {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
        )
    }
}
