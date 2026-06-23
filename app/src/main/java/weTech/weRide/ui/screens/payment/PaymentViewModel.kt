package weTech.weRide.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import weTech.weRide.data.models.bookings.BookingResource

/**
 * ViewModel for Payment Screen
 * Manages payment form state and validation
 */
class PaymentViewModel(
    private val planId: String
) : ViewModel() {

    // Payment method tabs
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CARD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Card payment fields
    private val _cardNumber = MutableStateFlow("")
    val cardNumber: StateFlow<String> = _cardNumber.asStateFlow()

    private val _cardHolder = MutableStateFlow("")
    val cardHolder: StateFlow<String> = _cardHolder.asStateFlow()

    private val _expiryDate = MutableStateFlow("")
    val expiryDate: StateFlow<String> = _expiryDate.asStateFlow()

    private val _cvv = MutableStateFlow("")
    val cvv: StateFlow<String> = _cvv.asStateFlow()

    // Yape payment fields
    private val _yapePhone = MutableStateFlow("")
    val yapePhone: StateFlow<String> = _yapePhone.asStateFlow()

    // Plin payment fields
    private val _plinPhone = MutableStateFlow("")
    val plinPhone: StateFlow<String> = _plinPhone.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Success state
    private val _isPaymentSuccessful = MutableStateFlow(false)
    val isPaymentSuccessful: StateFlow<Boolean> = _isPaymentSuccessful.asStateFlow()

    // Form validation states
    val isCardFormValid: StateFlow<Boolean> = combine(
        cardNumber, cardHolder, expiryDate, cvv
    ) { number, holder, expiry, cvv ->
        number.length >= 16 &&
        holder.isNotBlank() &&
        expiry.matches(Regex("\\d{2}/\\d{2}")) &&
        cvv.length >= 3
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isYapeFormValid: StateFlow<Boolean> = yapePhone.map { phone ->
        phone.matches(Regex("^\\+51\\d{9}$")) || phone.matches(Regex("^\\d{9}$"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isPlinFormValid: StateFlow<Boolean> = plinPhone.map { phone ->
        phone.matches(Regex("^\\+51\\d{9}$")) || phone.matches(Regex("^\\d{9}$"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isFormValid: StateFlow<Boolean> = combine(
        selectedPaymentMethod,
        isCardFormValid,
        isYapeFormValid,
        isPlinFormValid
    ) { method, cardValid, yapeValid, plinValid ->
        when (method) {
            PaymentMethod.CARD -> cardValid
            PaymentMethod.YAPE -> yapeValid
            PaymentMethod.PLIN -> plinValid
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Update payment method
     */
    fun updatePaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
        _error.value = null
    }

    /**
     * Update card number (formatted with spaces)
     */
    fun updateCardNumber(number: String) {
        val cleaned = number.filter { it.isDigit() }
        val formatted = buildString {
            cleaned.forEachIndexed { index, char ->
                if (index > 0 && index % 4 == 0) append(" ")
                append(char)
            }
        }
        _cardNumber.value = formatted.take(19) // Max 16 digits + 3 spaces
    }

    /**
     * Update card holder name
     */
    fun updateCardHolder(name: String) {
        _cardHolder.value = name.trim().take(30)
    }

    /**
     * Update expiry date (formatted MM/YY)
     */
    fun updateExpiryDate(date: String) {
        val cleaned = date.filter { it.isDigit() }
        if (cleaned.length <= 4) {
            val formatted = if (cleaned.length >= 2) {
                "${cleaned.substring(0, 2)}/${cleaned.substring(2)}"
            } else {
                cleaned
            }
            _expiryDate.value = formatted
        }
    }

    /**
     * Update CVV
     */
    fun updateCvv(cvv: String) {
        _cvv.value = cvv.filter { it.isDigit() }.take(4)
    }

    /**
     * Update Yape phone number
     */
    fun updateYapePhone(phone: String) {
        val cleaned = phone.filter { it.isDigit() }
        _yapePhone.value = cleaned.take(9)
    }

    /**
     * Update Plin phone number
     */
    fun updatePlinPhone(phone: String) {
        val cleaned = phone.filter { it.isDigit() }
        _plinPhone.value = cleaned.take(9)
    }

    /**
     * Submit payment
     */
    fun submitPayment() {
        if (!isFormValid.value) {
            _error.value = "Por favor completa todos los campos requeridos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Implement actual payment API call
                // For now, simulate payment processing
                kotlinx.coroutines.delay(2000)

                // Simulate success
                _isPaymentSuccessful.value = true
                _error.value = null

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al procesar el pago"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Reset payment form
     */
    fun resetForm() {
        _cardNumber.value = ""
        _cardHolder.value = ""
        _expiryDate.value = ""
        _cvv.value = ""
        _yapePhone.value = ""
        _plinPhone.value = ""
        _error.value = null
        _isPaymentSuccessful.value = false
    }
}

/**
 * Payment method enum
 */
enum class PaymentMethod(val displayName: String, val icon: String) {
    CARD("Tarjeta", "card"),
    YAPE("Yape", "yape"),
    PLIN("Plin", "plin")
}
