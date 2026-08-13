package ch.lkmc.wortkatze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ch.lkmc.wortkatze.ui.home.HomeScreen
import ch.lkmc.wortkatze.ui.theme.WortkatzeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WortkatzeTheme {
                HomeScreen()
            }
        }
    }
}
