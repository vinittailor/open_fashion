package com.example.open_fashion.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.open_fashion.ui.theme.AccentGold
import com.example.open_fashion.ui.theme.BorderLight
import com.example.open_fashion.ui.theme.SurfaceVariantLight
import kotlinx.coroutines.launch

/**
 * Enterprise Luxury Product Media Gallery Composable.
 * Features a high-performance HorizontalPager, smooth dot indicators,
 * "1 / N" Luxury counter badge, and an interactive thumbnail strip.
 *
 * @param imageUrls List of public image asset URLs
 * @param modifier Composable layout modifier
 * @param aspectRatio Aspect ratio of the hero image pager (default: 3/4 editorial fashion ratio)
 * @param onImageClick Optional callback when the active image is tapped
 */
@Composable
fun LuxuryProductGalleryPager(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 0.75f, // 3:4 High-Fashion Editorial Ratio
    onImageClick: ((index: Int) -> Unit)? = null
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            contentAlignment = Alignment.Center
        ) {
            LuxuryImage(
                imageUrl = null,
                contentDescription = "No product image",
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val thumbnailListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Keep thumbnail strip scrolled into view when pager page changes
    LaunchedEffect(pagerState.currentPage) {
        thumbnailListState.animateScrollToItem(pagerState.currentPage)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Hero Image Pager Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(4.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val url = imageUrls[page]
                LuxuryImage(
                    imageUrl = url,
                    contentDescription = "Product image ${page + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = onImageClick != null) {
                            onImageClick?.invoke(page)
                        },
                    contentScale = ContentScale.Crop
                )
            }

            // Floating "1 / N" Luxury Counter Badge in Top-Right
            if (imageUrls.size > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    LuxuryBadge(
                        text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
                        variant = BadgeVariant.GOLD
                    )
                }
            }

            // Dot Indicators at Bottom Center of Pager
            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(imageUrls.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 20.dp else 6.dp,
                            label = "dot_width"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (isSelected) AccentGold else Color.White.copy(alpha = 0.6f),
                            label = "dot_color"
                        )

                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }

        // 2. Interactive Thumbnail Selector Row (when multiple images exist)
        if (imageUrls.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                state = thumbnailListState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(imageUrls) { index, url ->
                    val isSelected = pagerState.currentPage == index
                    val borderColor = if (isSelected) AccentGold else BorderLight
                    val borderWidth = if (isSelected) 1.5.dp else 1.dp

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(
                                border = BorderStroke(borderWidth, borderColor),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    ) {
                        LuxuryImage(
                            imageUrl = url,
                            contentDescription = "Thumbnail ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Subtle dark overlay on non-selected thumbnails for contrast
                        if (!isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.15f))
                            )
                        }
                    }
                }
            }
        }
    }
}
