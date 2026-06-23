package weTech.weRide.ui.screens.qrscanner

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.EnumMap

/**
 * ViewModel for QR Scanner screen
 * Handles QR code detection and validation
 */
class QRScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<QRScannerUiState>(QRScannerUiState.Idle)
    val uiState: StateFlow<QRScannerUiState> = _uiState.asStateFlow()

    private val multiFormatReader = MultiFormatReader().apply {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        hints[DecodeHintType.POSSIBLE_FORMATS] = listOf(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39
        )
        hints[DecodeHintType.TRY_HARDER] = true
        setHints(hints)
    }

    /**
     * Process image from camera and detect QR code
     */
    fun processImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            try {
                val buffer = imageProxy.planes[0].buffer
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                val width = imageProxy.width
                val height = imageProxy.height

                // Rotate image if needed (portrait mode)
                val rotatedData = if (imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270) {
                    rotateData(data, width, height)
                } else {
                    data
                }

                val rotatedWidth = if (imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270) {
                    height
                } else {
                    width
                }

                val rotatedHeight = if (imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270) {
                    width
                } else {
                    height
                }

                val source = PlanarYUVLuminanceSource(
                    rotatedData,
                    rotatedWidth,
                    rotatedHeight,
                    0,
                    0,
                    rotatedWidth,
                    rotatedHeight,
                    false
                )

                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                val result = multiFormatReader.decodeWithState(binaryBitmap)

                _uiState.value = QRScannerUiState.Success(result.text)
            } catch (e: Exception) {
                // No QR code found, stay idle
            } finally {
                imageProxy.close()
            }
        }
    }

    /**
     * Rotate image data for portrait mode
     */
    private fun rotateData(data: ByteArray, width: Int, height: Int): ByteArray {
        val rotatedData = ByteArray(data.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                rotatedData[x * height + height - y - 1] = data[x + y * width]
            }
        }
        return rotatedData
    }

    /**
     * Validate QR code content
     * Expected format: "weride:vehicle:{vehicleId}:{bookingId}"
     */
    fun validateQRCode(qrCode: String): QRCodeData? {
        return try {
            val parts = qrCode.split(":")
            if (parts.size >= 4 && parts[0] == "weride" && parts[1] == "vehicle") {
                QRCodeData(
                    vehicleId = parts[2],
                    bookingId = parts[3].toLongOrNull() ?: 0L
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Reset scanner state
     */
    fun resetScanner() {
        _uiState.value = QRScannerUiState.Idle
    }

    /**
     * Handle scan error
     */
    fun handleError(message: String) {
        _uiState.value = QRScannerUiState.Error(message)
    }
}

/**
 * UI State for QR Scanner
 */
sealed class QRScannerUiState {
    object Idle : QRScannerUiState()
    data class Success(val qrCode: String) : QRScannerUiState()
    data class Error(val message: String) : QRScannerUiState()
}

/**
 * QR Code Data
 */
data class QRCodeData(
    val vehicleId: String,
    val bookingId: Long
)
