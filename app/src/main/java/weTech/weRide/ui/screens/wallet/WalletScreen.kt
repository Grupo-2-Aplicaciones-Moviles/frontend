package weTech.weRide.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import weTech.weRide.ui.components.WeRideButton
import weTech.weRide.ui.theme.*

/**
 * Wallet Screen
 * Shows user balance and transaction history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    viewModel: WalletViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddFundsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi billetera") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFundsDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar fondos")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EnergyGreen)
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Balance card
                BalanceCard(
                    balance = state.balance,
                    lastUpdated = state.lastUpdated,
                    onAddFunds = { showAddFundsDialog = true }
                )

                // Quick actions
                QuickActions(
                    onAddFunds = { showAddFundsDialog = true },
                    onTransfer = { /* TODO */ }
                )

                // Transaction history
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de transacciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { /* TODO: Show all */ }) {
                            Text("Ver todas")
                        }
                    }

                    if (state.transactions.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Sin transacciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Agrega fondos para comenzar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.transactions.take(10).forEach { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Add funds dialog
    if (showAddFundsDialog) {
        AddFundsDialog(
            onDismiss = { showAddFundsDialog = false },
            onConfirm = { amount ->
                viewModel.addFunds(amount) { success ->
                    showAddFundsDialog = false
                    // TODO: Show success/error message
                }
            }
        )
    }
}

/**
 * Balance card
 */
@Composable
fun BalanceCard(
    balance: Double,
    lastUpdated: String,
    onAddFunds: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnergyGreen
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Saldo disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = White
            )
            Text(
                text = String.format("S/ %.2f", balance),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = White
            )
            if (lastUpdated.isNotEmpty()) {
                Text(
                    text = "Actualizado $lastUpdated",
                    style = MaterialTheme.typography.bodySmall,
                    color = White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Quick actions row
 */
@Composable
fun QuickActions(
    onAddFunds: () -> Unit,
    onTransfer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.Add,
            title = "Agregar fondos",
            onClick = onAddFunds,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Default.Send,
            title = "Transferir",
            onClick = onTransfer,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Quick action card
 */
@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EnergyGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = EnergyGreen
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Transaction item
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (transaction.type) {
                                TransactionType.CREDIT -> SuccessGreen.copy(alpha = 0.1f)
                                TransactionType.DEBIT -> ErrorRed.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (transaction.type) {
                            TransactionType.CREDIT -> Icons.Default.Add
                            TransactionType.DEBIT -> Icons.Default.Remove
                        },
                        contentDescription = null,
                        tint = when (transaction.type) {
                            TransactionType.CREDIT -> SuccessGreen
                            TransactionType.DEBIT -> ErrorRed
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = transaction.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = String.format(
                    "%sS/ %.2f",
                    if (transaction.type == TransactionType.CREDIT) "+" else "-",
                    transaction.amount
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (transaction.type) {
                    TransactionType.CREDIT -> SuccessGreen
                    TransactionType.DEBIT -> ErrorRed
                }
            )
        }
    }
}

/**
 * Add funds dialog
 */
@Composable
fun AddFundsDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var selectedAmount by remember { mutableStateOf<Double?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }

    val quickAmounts = listOf(20.0, 50.0, 100.0, 200.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar fondos") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona un monto:",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isCustom) {
                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        label = { Text("Monto personalizado") },
                        leadingIcon = { Text("S/ ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickAmounts.chunked(2).forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { amount ->
                                    FilterChip(
                                        selected = selectedAmount == amount,
                                        onClick = { selectedAmount = amount },
                                        label = { Text("S/ $amount") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                TextButton(
                    onClick = {
                        isCustom = !isCustom
                        selectedAmount = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCustom) "Ver montos rápidos" else "Ingresar monto personalizado")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = if (isCustom) {
                        customAmount.toDoubleOrNull() ?: 0.0
                    } else {
                        selectedAmount ?: 0.0
                    }
                    if (amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = if (isCustom) {
                    customAmount.toDoubleOrNull() ?: 0.0 > 0
                } else {
                    selectedAmount != null
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
