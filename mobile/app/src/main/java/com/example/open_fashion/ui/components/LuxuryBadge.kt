package com.example.open_fashion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.open_fashion.ui.theme.*

/**
 * Standard semantic variants for Luxury Badges & Status Pills.
 */
enum class BadgeVariant {
    GOLD,    // Champagne gold for roles & premium tiers
    SUCCESS, // Emerald green for verified & in-stock
    WARNING, // Warm amber for low stock & pending
    ERROR,   // Crimson red for banned & out of stock
    NEUTRAL  // Charcoal / Alabaster for general metadata
}

/**
 * Enterprise Luxury Badge & Status Pill Composable.
 */
@Composable
fun LuxuryBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.GOLD,
    cornerRadius: Dp = 4.dp
) {
    val (backgroundColor, textColor, borderColor) = when (variant) {
        BadgeVariant.GOLD -> Triple(
            AccentGoldLight,
            AccentGoldDark,
            AccentGold.copy(alpha = 0.4f)
        )
        BadgeVariant.SUCCESS -> Triple(
            StatusSuccess.copy(alpha = 0.12f),
            StatusSuccess,
            StatusSuccess.copy(alpha = 0.3f)
        )
        BadgeVariant.WARNING -> Triple(
            StatusWarning.copy(alpha = 0.12f),
            StatusWarning,
            StatusWarning.copy(alpha = 0.3f)
        )
        BadgeVariant.ERROR -> Triple(
            StatusError.copy(alpha = 0.12f),
            StatusError,
            StatusError.copy(alpha = 0.3f)
        )
        BadgeVariant.NEUTRAL -> Triple(
            SurfaceVariantLight,
            TextSecondaryLight,
            BorderLight
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(0.5.dp, borderColor),
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = textColor
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
