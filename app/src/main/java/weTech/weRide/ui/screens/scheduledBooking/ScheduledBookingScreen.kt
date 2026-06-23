package weTech.weRide.ui.screens.scheduledBooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import weTech.weRide.data.models.vehicles.VehicleResource
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Scheduled Booking Screen
 * Allows users to schedule a vehicle unlock for a future time
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledBookingScreen(
    navController: NavController,
    viewModel: ScheduledBookingViewModel = koinViewModel {
        parametersOf(
            navController.previousBackStackEntry
                ?.arguments
                ?.getString("vehicleId") ?: "",
            1L // TODO: Get from auth state
        )
    }
) {
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val isLoadingVehicle by viewModel.isLoadingVehicle.collectAsStateWithLifecycle()
    val vehicleError by viewModel.vehicleError.collectAsStateWithLifecycle()

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()

    val isCheckingAvailability by viewModel.isCheckingAvailability.collectAsStateWithLifecycle()
    val isAvailable by viewModel.isAvailable.collectAsStateWithLifecycle()
    val availabilityMessage by viewModel.availabilityMessage.collectAsStateWithLifecycle()

    val isCreatingBooking by viewModel.isCreatingBooking.collectAsStateWithLifecycle()
    val bookingResult by viewModel.bookingResult.collectAsStateWithLifecycle()
    val bookingError by viewModel.bookingError.collectAsStateWithLifecycle()

    val estimatedCost by remember { derivedStateOf { viewModel.getEstimatedCost() } }

    // Date picker dialog state
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Selected date and time for pickers
    var pickerDate by remember { mutableStateOf(LocalDate.now()) }
    var pickerTime by remember { mutableStateOf(LocalTime.of(12, 0)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programar reserva") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoadingVehicle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EnergyGreen)
                    }
                }

                vehicleError != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = vehicleError ?: "Error al cargar el vehículo",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { navController.navigateUp() }) {
                                Text("Volver")
                            }
                        }
                    }
                }

                vehicle != null -> {
                    ScheduledBookingContent(
                        vehicle = vehicle!!,
                        selectedDate = selectedDate,
                        selectedDuration = selectedDuration,
                        isFormValid = isFormValid,
                        isCheckingAvailability = isCheckingAvailability,
                        isAvailable = isAvailable,
                        availabilityMessage = availabilityMessage,
                        isCreatingBooking = isCreatingBooking,
                        estimatedCost = estimatedCost,
                        formattedDate = viewModel.getFormattedDate(),
                        formattedTime = viewModel.getFormattedTime(),
                        formattedEndTime = viewModel.getFormattedEndTime(),
                        onDateClick = { showDatePicker = true },
                        onTimeClick = { showTimePicker = true },
                        onDurationChange = { viewModel.selectDuration(it) },
                        onCheckAvailability = { viewModel.checkAvailability() },
                        onConfirmBooking = {
                            viewModel.createScheduledBooking { bookingId ->
                                // Navigate to confirmation or home
                                navController.previousBackStackEntry?.destination?.route?.let {
                                    navController.navigate(it) {
                                        popUpTo(0)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDate = pickerDate
        )

        DatePickerDialog(
            onDateSelected = { millis ->
                millis?.let {
                    pickerDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    viewModel.selectDateTime(LocalDateTime.of(pickerDate, pickerTime))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                pickerTime = time
                viewModel.selectDateTime(LocalDateTime.of(pickerDate, pickerTime))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    // Booking error dialog
    bookingError?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.resetErrors() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetErrors() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Success dialog
    LaunchedEffect(bookingResult) {
        bookingResult?.let { _ ->
            // Navigate to confirmation screen or show success message
        }
    }
}

/**
 * Scheduled booking content
 */
@Composable
fun ScheduledBookingContent(
    vehicle: VehicleResource,
    selectedDate: LocalDateTime?,
    selectedDuration: Int,
    isFormValid: Boolean,
    isCheckingAvailability: Boolean,
    isAvailable: Boolean?,
    availabilityMessage: String?,
    isCreatingBooking: Boolean,
    estimatedCost: Double?,
    formattedDate: String?,
    formattedTime: String?,
    formattedEndTime: String?,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onDurationChange: (Int) -> Unit,
    onCheckAvailability: () -> Unit,
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Vehicle info card
        VehicleInfoCard(vehicle = vehicle)

        // Date/time selection section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona fecha y hora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Date selector
                DateSelector(
                    formattedDate = formattedDate,
                    onClick = onDateClick
                )

                HorizontalDivider()

                // Time selector
                TimeSelector(
                    formattedTime = formattedTime,
                    formattedEndTime = formattedEndTime,
                    onClick = onTimeClick
                )

                HorizontalDivider()

                // Duration selector
                DurationSelector(
                    selectedDuration = selectedDuration,
                    onDurationChange = onDurationChange
                )
            }
        }

        // Estimated cost card
        estimatedCost?.let { cost ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EnergyGreen.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Costo estimado",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "S/ ${String.format("%.2f", cost)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = EnergyGreen
                        )
                    }

                    Text(
                        text = "$selectedDuration min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Availability check card
        AvailabilityCheckCard(
            isFormValid = isFormValid,
            isCheckingAvailability = isCheckingAvailability,
            isAvailable = isAvailable,
            availabilityMessage = availabilityMessage,
            onCheckAvailability = onCheckAvailability
        )

        // Confirm booking button
        WeRideButton(
            text = "Confirmar reserva",
            onClick = onConfirmBooking,
            enabled = isAvailable == true && !isCreatingBooking,
            isLoading = isCreatingBooking,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Vehicle info card
 */
@Composable
fun VehicleInfoCard(vehicle: VehicleResource) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vehicle type icon
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp),
                color = getTypeColor(vehicle.type).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getTypeIcon(vehicle.type),
                        contentDescription = null,
                        tint = getTypeColor(vehicle.type),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehicle.getTypeDisplayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "S/ ${String.format("%.2f", vehicle.pricePerMinute)}/min",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnergyGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Date selector
 */
@Composable
fun DateSelector(
    formattedDate: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (formattedDate != null) {
                EnergyGreen.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                tint = if (formattedDate != null) EnergyGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fecha",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedDate ?: "Seleccionar fecha",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (formattedDate != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (formattedDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Time selector
 */
@Composable
fun TimeSelector(
    formattedTime: String?,
    formattedEndTime: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (formattedTime != null) {
                EnergyGreen.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Seleccionar hora",
                tint = if (formattedTime != null) EnergyGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hora",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (formattedTime != null && formattedEndTime != null) {
                    Text(
                        text = "$formattedTime - $formattedEndTime",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Seleccionar hora",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Duration selector
 */
@Composable
fun DurationSelector(
    selectedDuration: Int,
    onDurationChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Duración",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val durations = listOf(15, 30, 45, 60, 90, 120)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            durations.forEach { duration ->
                DurationChip(
                    duration = duration,
                    isSelected = selectedDuration == duration,
                    onClick = { onDurationChange(duration) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text(
            text = "Duración seleccionada: $selectedDuration minutos",
            style = MaterialTheme.typography.bodySmall,
            color = EnergyGreen,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Duration chip
 */
@Composable
fun DurationChip(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) EnergyGreen else MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Text(
            text = "${duration}m",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) White else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
    }
}

/**
 * Availability check card
 */
@Composable
fun AvailabilityCheckCard(
    isFormValid: Boolean,
    isCheckingAvailability: Boolean,
    isAvailable: Boolean?,
    availabilityMessage: String?,
    onCheckAvailability: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isAvailable == true -> SuccessGreen.copy(alpha = 0.1f)
                isAvailable == false -> ErrorRed.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        isAvailable == true -> Icons.Default.CheckCircle
                        isAvailable == false -> Icons.Default.Cancel
                        else -> Icons.Default.Search
                    },
                    contentDescription = null,
                    tint = when {
                        isAvailable == true -> SuccessGreen
                        isAvailable == false -> ErrorRed
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Text(
                    text = when {
                        isAvailable == true -> "Vehículo disponible"
                        isAvailable == false -> "Vehículo no disponible"
                        isCheckingAvailability -> "Verificando disponibilidad..."
                        else -> "Verificar disponibilidad"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isAvailable == true -> SuccessGreen
                        isAvailable == false -> ErrorRed
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            availabilityMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isCheckingAvailability && isAvailable == null) {
                WeRideButton(
                    text = "Verificar disponibilidad",
                    onClick = onCheckAvailability,
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isCheckingAvailability) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EnergyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Date picker dialog
 */
@Composable
fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDateSelected = onDateSelected,
        onDismiss = onDismiss
    )
}

/**
 * Time picker dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Get type icon
 */
private fun getTypeIcon(type: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type.lowercase()) {
        "scooter" -> Icons.Default.ElectricScooter
        "bike" -> Icons.Default.DirectionsBike
        "motorcycle" -> Icons.Default.TwoWheeler
        else -> Icons.Default.ElectricScooter
    }
}

/**
 * Get type color
 */
private fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "scooter" -> ScooterGreen
        "bike" -> BikeBlue
        "motorcycle" -> MotorcycleOrange
        else -> EnergyGreen
    }
}
