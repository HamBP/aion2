package me.algosketch.aioninfo

import aioninfo.composeapp.generated.resources.Res
import aioninfo.composeapp.generated.resources.notosanskr_bold
import aioninfo.composeapp.generated.resources.notosanskr_medium
import aioninfo.composeapp.generated.resources.notosanskr_regular
import aioninfo.composeapp.generated.resources.notosanskr_semibold
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.algosketch.aioninfo.presentation.feature.enhancement.EnhancementScreen
import org.jetbrains.compose.resources.Font

val NotoSansKR: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.notosanskr_regular, FontWeight.Normal),
        Font(Res.font.notosanskr_medium, FontWeight.Medium),
        Font(Res.font.notosanskr_semibold, FontWeight.SemiBold),
        Font(Res.font.notosanskr_bold, FontWeight.Bold),
    )

@Composable
private fun appTypography(): Typography {
    val fontFamily = NotoSansKR
    return Typography().run {
        copy(
            displayLarge = displayLarge.copy(fontFamily = fontFamily),
            displayMedium = displayMedium.copy(fontFamily = fontFamily),
            displaySmall = displaySmall.copy(fontFamily = fontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
            titleLarge = titleLarge.copy(fontFamily = fontFamily),
            titleMedium = titleMedium.copy(fontFamily = fontFamily),
            titleSmall = titleSmall.copy(fontFamily = fontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
            bodySmall = bodySmall.copy(fontFamily = fontFamily),
            labelLarge = labelLarge.copy(fontFamily = fontFamily),
            labelMedium = labelMedium.copy(fontFamily = fontFamily),
            labelSmall = labelSmall.copy(fontFamily = fontFamily),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(typography = appTypography()) {
        ContentArea {
            EnhancementScreen()
        }
    }
}

@Composable
fun ContentArea(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(modifier = Modifier.widthIn(max = 1080.dp)) {
            content()
        }
    }
}