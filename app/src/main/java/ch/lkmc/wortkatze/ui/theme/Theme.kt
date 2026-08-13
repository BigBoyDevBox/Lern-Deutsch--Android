package ch.lkmc.wortkatze.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The Wortkatze theme: light, round and sweet.
 *
 * **Light-only on purpose** (docs/decisions/0003). A kawaii candy surface is a
 * light design; the dark version is a different design rather than the same one
 * with inverted tokens, and shipping a mechanical inversion would look broken.
 * [isSystemInDarkTheme] is deliberately ignored — don't wire it up without the
 * dark palette the decision record asks for.
 */
private val CandyColors = lightColorScheme(
    primary = Candy.Bubblegum,
    onPrimary = Candy.Card,
    primaryContainer = Candy.Blush,
    onPrimaryContainer = Candy.Ink,
    secondary = Candy.Der,
    onSecondary = Candy.Card,
    tertiary = Candy.Das,
    onTertiary = Candy.Card,
    background = Candy.Cream,
    onBackground = Candy.Ink,
    surface = Candy.Card,
    onSurface = Candy.Ink,
    surfaceVariant = Candy.Lavender,
    onSurfaceVariant = Candy.InkSoft,
    error = Candy.Wrong,
    onError = Candy.Card,
)

/** Everything is a rounded sticker; nothing in this app has a sharp corner. */
private val CandyShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36.dp),
)

/**
 * System fonts only. The app has to render German, Simplified Chinese and
 * English side by side, and no bundled Latin font covers CJK — the device font
 * does, in every weight, for zero bytes. Size and weight carry the personality
 * instead: big, heavy, generously spaced.
 */
private val CandyType = Typography(
    displayLarge = TextStyle(fontSize = 52.sp, lineHeight = 58.sp, fontWeight = FontWeight.Black),
    displayMedium = TextStyle(fontSize = 40.sp, lineHeight = 46.sp, fontWeight = FontWeight.Black),
    headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.ExtraBold),
    headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun WortkatzeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CandyColors,
        shapes = CandyShapes,
        typography = CandyType,
        content = content,
    )
}
