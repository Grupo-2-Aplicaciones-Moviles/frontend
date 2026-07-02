package weTech.weRide.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import weTech.weRide.data.pref.TokenManager
import java.text.SimpleDateFormat
import java.util.*

data class WalletState(
    val balance: Double = 0.0,
    val lastUpdated: String = "",
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: String
)

enum class TransactionType {
    CREDIT, DEBIT
}

data class StoredTransaction(
    val id: String,
    val type: String,
    val amount: Double,
    val description: String,
    val createdAt: String
)

class WalletViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    init {
        loadWalletData()
    }

    fun loadWalletData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val balance = tokenManager.getWalletBalance().first() ?: 0.0
                val storedTransactions = tokenManager.getWalletTransactions().first()
                val lastUpdated = tokenManager.getWalletLastUpdated().first()

                val lastUpdatedFormatted = if (lastUpdated != null) {
                    formatLastUpdated(lastUpdated)
                } else {
                    "Nunca"
                }

                _state.value = WalletState(
                    balance = balance,
                    lastUpdated = lastUpdatedFormatted,
                    transactions = storedTransactions.map { it.toTransaction() },
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = WalletState(
                    balance = 0.0,
                    lastUpdated = "",
                    transactions = emptyList(),
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun addFunds(amount: Double, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val currentBalance = state.value.balance
                val newBalance = currentBalance + amount

                val newTransaction = StoredTransaction(
                    id = UUID.randomUUID().toString(),
                    type = "CREDIT",
                    amount = amount,
                    description = "Recarga de saldo",
                    createdAt = System.currentTimeMillis().toString()
                )

                val updatedTransactions = listOf(newTransaction) + state.value.transactions.map { it.toStoredTransaction() }
                tokenManager.saveWalletData(newBalance, updatedTransactions)

                loadWalletData()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun addDebitTransaction(amount: Double, description: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val currentBalance = state.value.balance
                val newBalance = maxOf(0.0, currentBalance - amount)

                val newTransaction = StoredTransaction(
                    id = UUID.randomUUID().toString(),
                    type = "DEBIT",
                    amount = amount,
                    description = description,
                    createdAt = System.currentTimeMillis().toString()
                )

                val updatedTransactions = listOf(newTransaction) + state.value.transactions.map { it.toStoredTransaction() }
                tokenManager.saveWalletData(newBalance, updatedTransactions)

                loadWalletData()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    private fun formatLastUpdated(timestamp: String): String {
        return try {
            val millis = timestamp.toLongOrNull() ?: return ""
            val date = Date(millis)
            val formatter = SimpleDateFormat("'Hoy a las' HH:mm", Locale("es", "PE"))
            formatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}

private fun StoredTransaction.toTransaction(): Transaction {
    val type = when (this.type.lowercase()) {
        "credit" -> TransactionType.CREDIT
        else -> TransactionType.DEBIT
    }

    val date = try {
        val millis = this.createdAt.toLongOrNull() ?: 0
        val parsedDate = Date(millis)
        val outputFormatter = SimpleDateFormat("d MMM, HH:mm", Locale("es", "PE"))
        outputFormatter.format(parsedDate)
    } catch (e: Exception) {
        this.createdAt
    }

    return Transaction(
        id = id,
        type = type,
        amount = amount,
        description = description,
        date = date
    )
}

private fun Transaction.toStoredTransaction(): StoredTransaction {
    return StoredTransaction(
        id = id,
        type = type.name,
        amount = amount,
        description = description,
        createdAt = System.currentTimeMillis().toString()
    )
}
