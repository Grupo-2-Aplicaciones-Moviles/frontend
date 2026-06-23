package weTech.weRide.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import weTech.weRide.ui.theme.WeRideTheme

/**
 * WeRide Card
 * Custom card component with consistent styling
 */
@Composable
fun WeRideCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke? = null,
    elevation: Int = 1,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = backgroundColor,
            border = border,
            tonalElevation = elevation.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    }
}

/**
 * WeRide Image Card
 * Card with an image at the top
 */
@Composable
fun WeRideImageCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    image: @Composable () -> Unit,
    title: String? = null,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            image()
            if (title != null || subtitle != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    title?.let {
                        androidx.compose.material3.Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    subtitle?.let {
                        androidx.compose.material3.Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * WeRide Info Card
 * Simple info display card
 */
@Composable
fun WeRideInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null
) {
    WeRideCard(modifier = modifier) {
        if (icon != null) {
            icon()
        }
        androidx.compose.material3.Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        subtitle?.let {
            androidx.compose.material3.Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideCardPreview() {
    WeRideTheme {
        WeRideInfoCard(
            title = "Card Title",
            subtitle = "Card subtitle text here"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideInfoCardPreview() {
    WeRideTheme {
        WeRideInfoCard(
            title = "Info Card",
            subtitle = "This is an info card with a subtitle"
        )
    }
}
