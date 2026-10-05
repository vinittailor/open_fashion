package com.example.open_fashion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.open_fashion.ui.theme.*

/**
 * Editorial Luxury Card Container.
 * Standardizes hairline 1px borders, subtle surface backgrounds, and sharp corners.
 */
@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 4.dp,
    containerColor: Color = SurfaceLight,
    borderColor: Color = BorderLight,
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
