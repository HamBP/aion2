package me.algosketch.aioninfo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import me.algosketch.aioninfo.enhancement.EnhancementScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        EnhancementScreen()
    }
}