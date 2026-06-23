package weTech.weRide.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Rating Bar Component
 * Displays star ratings
 */
@Composable
fun RatingBar(
    rating: Double,
    maxRating: Int = 5,
    modifier: Modifier = Modifier,
    starSize: Int = 20,
    isEditable: Boolean = false,
    onRatingChange: (Int) -> Unit = {}
) {
    val ratingInt = rating.toInt()

    Row(modifier = modifier) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < ratingInt) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.Star
                },
                contentDescription = null,
                tint = when {
                    index < ratingInt -> EnergyGreen
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                modifier = Modifier
                    .width(starSize.dp)
                    .height(starSize.dp)
            )

            if (isEditable) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

/**
 * Interactive Rating Bar
 * Allow users to select a rating
 */
@Composable
fun InteractiveRatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(modifier = modifier) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.Star
                },
                contentDescription = "${index + 1} estrellas",
                tint = if (index < rating) {
                    EnergyGreen
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .clickable { onRatingChange(index + 1) }
            )
        }
    }
}

/**
 * Rating Display
 * Shows rating with text
 */
@Composable
fun RatingDisplay(
    rating: Double,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        RatingBar(
            rating = rating,
            starSize = 16
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        reviewCount?.let {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "($it)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    WeRideTheme {
        RatingBar(
            rating = 4.5,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InteractiveRatingBarPreview() {
    WeRideTheme {
        InteractiveRatingBar(
            rating = 3,
            onRatingChange = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingDisplayPreview() {
    WeRideTheme {
        RatingDisplay(
            rating = 4.5,
            reviewCount = 128,
            modifier = Modifier.padding(16.dp)
        )
    }
}
