package com.example.open_fashion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.open_fashion.ui.theme.*

/**
 * Standard visual variants for Open Fashion Luxury Buttons.
 */
enum class ButtonVariant {
    PRIMARY,     // Champagne Gold background + Charcoal text
    SECONDARY,   // Obsidian Charcoal background + White text
    OUTLINE,     // Hairline border + Transparent background
    DESTRUCTIVE, // Crimson Red Outline / Background for destructive operations
    GHOST        // Borderless, text & icon only
}

/**
 * Enterprise Luxury Button Composable.
 * Standardizes architectural corners, loading spinners, typography tracking, and variant styles.
 */
@Composable
fun LuxuryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    height: Dp = 50.dp,
    cornerRadius: Dp = 2.dp
) {
    val shape = RoundedCornerShape(cornerRadius)

    val (containerColor, contentColor, borderStroke) = when (variant) {
        ButtonVariant.PRIMARY -> Triple(
            AccentGold,
            PrimaryCharcoal,
            null
        )
        ButtonVariant.SECONDARY -> Triple(
            PrimaryCharcoal,
            Color.White,
            null
        )
        ButtonVariant.OUTLINE -> Triple(
            Color.Transparent,
            MaterialTheme.colorScheme.onBackground,
            BorderStroke(1.dp, BorderLight)
        )
        ButtonVariant.DESTRUCTIVE -> Triple(
            Color.Transparent,
            StatusError,
            BorderStroke(1.dp, StatusError.copy(alpha = 0.35f))
        )
        ButtonVariant.GHOST -> Triple(
            Color.Transparent,
            AccentGoldDark,
            null
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.4f)
        ),
        border = borderStroke,
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
    }
}
