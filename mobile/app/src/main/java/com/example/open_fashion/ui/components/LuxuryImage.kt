package com.example.open_fashion.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.open_fashion.ui.theme.AccentGoldLight
import com.example.open_fashion.ui.theme.BorderLight
import com.example.open_fashion.ui.theme.SurfaceVariantLight
import com.example.open_fashion.ui.theme.TextTertiaryLight

/**
 * Creates a shimmering animated luxury brush for placeholders.
 * Smoothly sweeps a soft Champagne Gold shimmer across an Alabaster base.
 */
@Composable
fun rememberLuxuryShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "luxury_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    return Brush.linearGradient(
        colors = listOf(
            SurfaceVariantLight,
            AccentGoldLight.copy(alpha = 0.45f),
            SurfaceVariantLight
        ),
        start = Offset(x = translateAnim - 300f, y = translateAnim - 300f),
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/**
 * Centralized Luxury Image Loading Component for Android Jetpack Compose.
 * Uses Coil 2.7.0 for disk/memory caching with luxury shimmer animation placeholders.
 * Adheres to ADR-007 design system standards.
 *
 * @param imageUrl Fully qualified URL to image asset
 * @param contentDescription Accessibility description
 * @param modifier Composable layout modifier
 * @param contentScale Scaling behavior (default: Crop)
 * @param cornerRadius Corner rounding radius (default: 4.dp)
 * @param shape Custom shape if specified
 */
@Composable
fun LuxuryImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(cornerRadius)
) {
    val context = LocalContext.current
    val shimmerBrush = rememberLuxuryShimmerBrush()

    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(300)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale,
        loading = {
            // Elegant luxury shimmer placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(shimmerBrush)
            )
        },
        error = {
            // Subtle fallback container for broken or missing images
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceVariantLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.BrokenImage,
                    contentDescription = "Image unavailable",
                    tint = TextTertiaryLight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}
