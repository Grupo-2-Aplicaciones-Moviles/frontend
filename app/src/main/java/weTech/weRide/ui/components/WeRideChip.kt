package weTech.weRide.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * WeRide Filter Chip
 * For filtering options (can be selected/deselected)
 */
@Composable
fun WeRideFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = if (selected && leadingIcon != null) {
            {
                leadingIcon()
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EnergyGreen.copy(alpha = 0.2f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        border = if (!selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        } else null,
        shape = MaterialTheme.shapes.small
    )
}

/**
 * WeRide Input Chip
 * For selected items with dismiss option
 */
@Composable
fun WeRideInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        shape = MaterialTheme.shapes.small
    )
}

/**
 * WeRide Category Chip Group
 * Group of filter chips for categories
 */
@Composable
fun WeRideCategoryChipGroup(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            WeRideFilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = category
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideFilterChipPreview() {
    WeRideTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            WeRideFilterChip(
                selected = true,
                onClick = { },
                label = "Scooter"
            )
            WeRideFilterChip(
                selected = false,
                onClick = { },
                label = "Bike"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideCategoryChipGroupPreview() {
    WeRideTheme {
        val categories = listOf("All", "Scooter", "Bike", "Motorcycle")
        WeRideCategoryChipGroup(
            categories = categories,
            selectedCategory = "Scooter",
            onCategorySelected = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}
