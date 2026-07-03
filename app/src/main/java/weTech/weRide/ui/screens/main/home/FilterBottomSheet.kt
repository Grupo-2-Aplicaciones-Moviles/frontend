package weTech.weRide.ui.screens.main.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.components.WeRideOutlinedButton
import weTech.weRide.ui.theme.EnergyGreen

/**
 * Filter bottom sheet for vehicles
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedType: String?,
    selectedMinBattery: Int,
    onTypeSelected: (String?) -> Unit,
    onMinBatteryChanged: (Int) -> Unit,
    onClearFilters: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        sheetContent = {
            FilterSheetContent(
                selectedType = selectedType,
                selectedMinBattery = selectedMinBattery,
                onTypeSelected = onTypeSelected,
                onMinBatteryChanged = onMinBatteryChanged,
                onClearFilters = onClearFilters,
                onApply = onApply,
                onDismiss = onDismiss
            )
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 8.dp,
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
    }
}

/**
 * Filter sheet content
 */
@Composable
fun FilterSheetContent(
    selectedType: String?,
    selectedMinBattery: Int,
    onTypeSelected: (String?) -> Unit,
    onMinBatteryChanged: (Int) -> Unit,
    onClearFilters: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicleTypes = listOf(
        "Todos" to null,
        "Scooter" to "scooter",
        "Bicicleta" to "bike",
        "Motocicleta" to "motorcycle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }

        // Type filter
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tipo de vehículo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            vehicleTypes.forEach { (label, value) ->
                FilterChip(
                    selected = selectedType == value,
                    onClick = { onTypeSelected(value) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedType == value) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EnergyGreen,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Divider
        HorizontalDivider()

        // Battery filter
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Batería mínima: $selectedMinBattery%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Slider(
                value = selectedMinBattery.toFloat(),
                onValueChange = { onMinBatteryChanged(it.toInt()) },
                valueRange = 0f..100f,
                steps = 10,
                colors = SliderDefaults.colors(
                    activeTrackColor = EnergyGreen,
                    thumbColor = EnergyGreen
                )
            )

            // Battery indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(0, 25, 50, 75, 100).forEach { value ->
                    Text(
                        text = "$value%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Divider
        HorizontalDivider()

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeRideOutlinedButton(
                onClick = onClearFilters,
                modifier = Modifier.weight(1f),
                text = "Limpiar"
            )

            WeRideButton(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                text = "Aplicar"
            )
        }
    }
}
