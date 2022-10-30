package com.strand.finaid.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.color.MaterialColors


fun composeHarmonize(colorToHarmonize: Color, colorToHarmonizeWith: Color): Color {
    val androidColorToHarmonize = colorToHarmonize.toArgb()
    val androidColorToHarmonizeWith = colorToHarmonizeWith.toArgb()
    val androidHarmonizedColor = MaterialColors.harmonize(androidColorToHarmonize, androidColorToHarmonizeWith)
    return Color(androidHarmonizedColor)
}

fun composeGetColorRoles(color: Color, isLightTheme: Boolean): ComposeColorRoles {
    val androidColor = color.toArgb()
    val androidColorRoles = MaterialColors.getColorRoles(androidColor, isLightTheme)
    return ComposeColorRoles(
        accent = Color(androidColorRoles.accent),
        onAccent = Color(androidColorRoles.onAccent),
        accentContainer = Color(androidColorRoles.accentContainer),
        onAccentContainer = Color(androidColorRoles.onAccentContainer)
    )
}

data class ComposeColorRoles(
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color
)

fun Color.asHexCode() = String.format("%06X", this.toArgb() and 0xFFFFFF)

fun String.asColor() = Color(android.graphics.Color.parseColor("#${this}"))


