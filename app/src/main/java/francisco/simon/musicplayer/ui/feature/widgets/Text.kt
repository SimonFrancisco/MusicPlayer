package francisco.simon.musicplayer.ui.feature.widgets

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedText(
    @StringRes textResourceId: Int,
    spanStyle: SpanStyle,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textAlign: TextAlign? = null
) {
    val rawText = stringResource(textResourceId)
    val annotatedString = buildAnnotatedString {
        val regex = "<highlight>(.*?)</highlight>".toRegex()
        var lastIndex = 0
        for (match in regex.findAll(rawText)) {
            val start = match.range.first
            val end = match.range.last + 1
            val beforeText = rawText.substring(startIndex = lastIndex, endIndex = start)
            val highlightedText = match.groupValues[1] ?: ""
            append(beforeText)
            withStyle(style = spanStyle) {
                append(highlightedText)
            }
            lastIndex = end
        }
        if (lastIndex < rawText.length) {
            append(rawText.substring(startIndex = lastIndex))
        }
    }
    Text(text = annotatedString, modifier = modifier, textAlign = textAlign, style = textStyle)
}