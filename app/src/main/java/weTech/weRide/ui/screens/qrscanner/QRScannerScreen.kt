package weTech.weRide.ui.screens.qrscanner

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.theme.EnergyGreen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.ui.text.font.FontWeight
import org.koin.androidx.compose.koinViewModel

/**
 * QR Scanner Screen for Vehicle Unlock
 * Uses CameraX to scan QR codes on vehicles
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    navController: NavHostController,
    bookingId: Long,
    viewModel: QRScannerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status !is PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear código QR") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            flashEnabled = !flashEnabled
                            cameraProvider?.let { cp ->
                                cp.unbindAll()
                                bindCamera(
                                    context = context,
                                    lifecycleOwner = lifecycleOwner,
                                    previewView = null,
                                    cameraProvider = cp,
                                    flashEnabled = flashEnabled,
                                    onQrDetected = { qrCode ->
                                        val qrData = viewModel.validateQRCode(qrCode)
                                        if (qrData != null && qrData.bookingId == bookingId) {
                                            // Valid QR code for this booking, navigate to active trip
                                            navController.navigate(Screen.ActiveTrip.createRoute(bookingId))
                                        } else {
                                            viewModel.handleError("Código QR inválido o no corresponde a esta reserva")
                                        }
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    ) {
                        Icon(
                            if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = if (flashEnabled) "Apagar flash" else "Encender flash"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraPermissionState.status is PermissionStatus.Granted) {
                // Camera Preview
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { previewView ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            cameraProvider = cameraProviderFuture.get()
                            bindCamera(
                                context = context,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                cameraProvider = cameraProvider!!,
                                flashEnabled = flashEnabled,
                                onQrDetected = { qrCode ->
                                    val qrData = viewModel.validateQRCode(qrCode)
                                    if (qrData != null && qrData.bookingId == bookingId) {
                                        // Valid QR code for this booking
                                        navController.navigate(Screen.ActiveTrip.createRoute(bookingId))
                                    } else {
                                        viewModel.handleError("Código QR inválido o no corresponde a esta reserva")
                                    }
                                },
                                viewModel = viewModel
                            )
                        }, context.mainExecutor)
                    }
                )

                // QR Scanner Overlay
                QRScannerOverlay(modifier = Modifier.fillMaxSize())

                // Instructions
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Apunta la cámara al código QR del vehículo",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }

                // Error message
                if (uiState is QRScannerUiState.Error) {
                    val error = (uiState as QRScannerUiState.Error)
                    AlertDialog(
                        onDismissRequest = { viewModel.resetScanner() },
                        title = { Text("Error") },
                        text = { Text(error.message) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.resetScanner() }) {
                                Text("Reintentar")
                            }
                        }
                    )
                }
            } else {
                // Permission denied
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Permiso de cámara requerido",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Necesitas otorgar el permiso de cámara para escanear códigos QR y desbloquear vehículos.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    WeRideButton(
                        text = "Otorgar permiso",
                        onClick = { cameraPermissionState.launchPermissionRequest() }
                    )
                }
            }
        }
    }
}

/**
 * Bind camera with preview and image analysis
 */
private fun bindCamera(
    context: android.content.Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView?,
    cameraProvider: ProcessCameraProvider,
    flashEnabled: Boolean,
    onQrDetected: (String) -> Unit,
    viewModel: QRScannerViewModel
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView?.surfaceProvider)
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
        .also {
            it.setAnalyzer(context.mainExecutor) { imageProxy ->
                viewModel.processImage(imageProxy)

                // Handle successful scan
                val uiState = viewModel.uiState.value
                if (uiState is QRScannerUiState.Success && !imageProxy.imageInfo.rotationDegrees.toString().contains("processed")) {
                    onQrDetected(uiState.qrCode)
                }
            }
        }

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    } catch (e: Exception) {
        // Handle camera binding error
    }
}

/**
 * QR Scanner Overlay UI
 */
@Composable
fun QRScannerOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // Scanning frame
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .background(Color.Transparent)
        ) {
            // Corner markers
            CornerRadius(position = "top-left")
            CornerRadius(position = "top-right")
            CornerRadius(position = "bottom-left")
            CornerRadius(position = "bottom-right")

            // Scanning line animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(EnergyGreen)
                    .align(Alignment.TopCenter)
            )
        }

        // Bottom instructions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text(
                text = "Coloca el código QR dentro del recuadro",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El escaneo es automático",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Corner radius for QR scanner frame
 */
@Composable
fun BoxScope.CornerRadius(position: String) {
    val modifier = when (position) {
        "top-left" -> Modifier
            .align(Alignment.TopStart)
            .offset(x = (-1).dp, y = (-1).dp)
        "top-right" -> Modifier
            .align(Alignment.TopEnd)
            .offset(x = 1.dp, y = (-1).dp)
        "bottom-left" -> Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-1).dp, y = 1.dp)
        "bottom-right" -> Modifier
            .align(Alignment.BottomEnd)
            .offset(x = 1.dp, y = 1.dp)
        else -> Modifier
    }

    Box(modifier = modifier) {
        when (position) {
            "top-left" -> CornerMarker(horizontal = true, vertical = true)
            "top-right" -> CornerMarker(horizontal = true, vertical = false)
            "bottom-left" -> CornerMarker(horizontal = false, vertical = true)
            "bottom-right" -> CornerMarker(horizontal = false, vertical = false)
        }
    }
}

/**
 * Individual corner marker
 */
@Composable
fun BoxScope.CornerMarker(horizontal: Boolean, vertical: Boolean) {
    val horizontalSize = if (horizontal) 40.dp else 20.dp
    val verticalSize = if (vertical) 40.dp else 20.dp

    Box(
        modifier = Modifier
            .size(width = horizontalSize, height = verticalSize)
            .background(Color.Transparent)
    ) {
        // Horizontal line
        Box(
            modifier = Modifier
                .then(if (horizontal) Modifier.fillMaxWidth() else Modifier.width(4.dp))
                .height(4.dp)
                .background(EnergyGreen)
                .then(
                    if (horizontal) {
                        if (vertical) Modifier.align(Alignment.CenterStart)
                        else Modifier.align(Alignment.CenterStart)
                    } else {
                        Modifier
                    }
                )
                .then(if (!horizontal && !vertical) Modifier.align(Alignment.CenterStart) else Modifier)
        )

        // Vertical line
        Box(
            modifier = Modifier
                .width(4.dp)
                .then(if (vertical) Modifier.fillMaxHeight() else Modifier.height(4.dp))
                .background(EnergyGreen)
                .then(if (vertical && horizontal) Modifier.align(Alignment.TopStart) else Modifier)
                .then(if (vertical && !horizontal) Modifier.align(Alignment.TopStart) else Modifier)
                .then(if (!vertical && horizontal) Modifier.align(Alignment.BottomEnd) else Modifier)
        )
    }
}
