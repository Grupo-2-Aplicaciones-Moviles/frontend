package weTech.weRide.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Skeleton loading animation component
 * Provides shimmer effect for loading states
 */

/**
 * Basic skeleton box with shimmer effect
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.5f)
) {
    val shimmerColors = listOf(
        color,
        highlightColor,
        color
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    Box(
        modifier = modifier
            .background(color)
            .graphicsLayer {
                translationX = translateAnimation
            }
    )
}

/**
 * Skeleton text placeholder
 */
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    width: Dp = 100.dp
) {
    SkeletonBox(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
    )
}

/**
 * Skeleton circle placeholder
 */
@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    SkeletonBox(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}

/**
 * Skeleton card placeholder
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp,
    cornerRadius: Dp = 16.dp
) {
    SkeletonBox(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
    )
}

/**
 * Skeleton vehicle card for garage/home screens
 */
@Composable
fun SkeletonVehicleCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonCircle(size = 48.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonText(width = 120.dp, height = 16.dp)
                    SkeletonText(width = 80.dp, height = 14.dp)
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonCircle(size = 24.dp)
                    SkeletonText(width = 40.dp, height = 12.dp)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonCircle(size = 24.dp)
                    SkeletonText(width = 40.dp, height = 12.dp)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonCircle(size = 24.dp)
                    SkeletonText(width = 40.dp, height = 12.dp)
                }
            }
        }
    }
}

/**
 * Skeleton list item for trip history, wallet transactions, etc.
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonCircle(size = 48.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkeletonText(width = 150.dp, height = 16.dp)
            SkeletonText(width = 100.dp, height = 14.dp)
        }
        SkeletonText(width = 60.dp, height = 16.dp)
    }
}

/**
 * Skeleton stats card for profile/home screens
 */
@Composable
fun SkeletonStatsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkeletonCircle(size = 32.dp)
                    SkeletonText(width = 40.dp, height = 14.dp)
                    SkeletonText(width = 60.dp, height = 12.dp)
                }
            }
        }
    }
}

/**
 * Skeleton map placeholder for home screen
 */
@Composable
fun SkeletonMap(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        SkeletonBox(modifier = Modifier.fillMaxSize())

        // Map markers skeleton
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 80.dp, y = 100.dp)
        ) {
            SkeletonCircle(size = 32.dp)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-80).dp, y = 150.dp)
        ) {
            SkeletonCircle(size = 32.dp)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-50).dp)
        ) {
            SkeletonCircle(size = 32.dp)
        }
    }
}

/**
 * Full screen skeleton loader
 */
@Composable
fun SkeletonFullScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonCircle(size = 40.dp)
            SkeletonText(width = 150.dp, height = 20.dp)
        }

        // Stats
        SkeletonStatsCard()

        // List items
        repeat(3) {
            SkeletonListItem()
        }
    }
}

/**
 * Skeleton detail screen (for vehicle detail, trip summary, etc.)
 */
@Composable
fun SkeletonDetailScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image skeleton
        SkeletonCard(height = 200.dp)

        // Title section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonText(width = 200.dp, height = 24.dp)
            SkeletonText(width = 150.dp, height = 16.dp)
        }

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SkeletonCircle(size = 24.dp)
                        SkeletonText(width = 40.dp, height = 14.dp)
                    }
                }
            }
        }

        // Details
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkeletonText(width = 100.dp, height = 16.dp)
                    SkeletonText(width = 200.dp, height = 14.dp)
                }
            }
        }

        // Action button
        SkeletonCard(height = 56.dp)
    }
}
