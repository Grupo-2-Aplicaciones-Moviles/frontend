package weTech.weRide.ui.screens.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import weTech.weRide.ui.theme.*

/**
 * Settings option
 */
data class SettingOption(
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val type: SettingType
)

sealed class SettingType {
    data class Toggle(val initialValue: Boolean) : SettingType()
    data class Navigation(val route: String) : SettingType()
    data class Info(val value: String) : SettingType()
}

/**
 * Settings Screen
 * Shows app settings and preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    // Settings states
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var locationEnabled by remember { mutableStateOf(true) }
    var emailMarketingEnabled by remember { mutableStateOf(false) }

    val settingsGroups = remember {
        listOf(
            "General" to listOf(
                SettingOption(
                    title = "Modo oscuro",
                    description = "Activar tema oscuro",
                    icon = Icons.Default.DarkMode,
                    type = SettingType.Toggle(darkModeEnabled)
                ),
                SettingOption(
                    title = "Idioma",
                    description = "Español (Perú)",
                    icon = Icons.Default.Language,
                    type = SettingType.Info("Español")
                ),
                SettingOption(
                    title = "Moneda",
                    description = "Soles peruanos",
                    icon = Icons.Default.AttachMoney,
                    type = SettingType.Info("PEN")
                )
            ),
            "Notificaciones" to listOf(
                SettingOption(
                    title = "Notificaciones push",
                    description = "Recibir alertas de viajes",
                    icon = Icons.Default.Notifications,
                    type = SettingType.Toggle(notificationsEnabled)
                ),
                SettingOption(
                    title = "Marketing por email",
                    description = "Ofertas y promociones",
                    icon = Icons.Default.Email,
                    type = SettingType.Toggle(emailMarketingEnabled)
                )
            ),
            "Privacidad" to listOf(
                SettingOption(
                    title = "Ubicación",
                    description = "Permitir acceso a ubicación",
                    icon = Icons.Default.LocationOn,
                    type = SettingType.Toggle(locationEnabled)
                ),
                SettingOption(
                    title = "Términos y condiciones",
                    icon = Icons.Default.Description,
                    type = SettingType.Navigation("terms")
                ),
                SettingOption(
                    title = "Política de privacidad",
                    icon = Icons.Default.Security,
                    type = SettingType.Navigation("privacy")
                )
            ),
            "Soporte" to listOf(
                SettingOption(
                    title = "Centro de ayuda",
                    description = "Preguntas frecuentes",
                    icon = Icons.Default.Help,
                    type = SettingType.Navigation("help")
                ),
                SettingOption(
                    title = "Contactar soporte",
                    description = "soporte@weride.pe",
                    icon = Icons.Default.SupportAgent,
                    type = SettingType.Navigation("support")
                ),
                SettingOption(
                    title = "Reportar un problema",
                    icon = Icons.Default.BugReport,
                    type = SettingType.Navigation("report")
                )
            ),
            "Acerca de" to listOf(
                SettingOption(
                    title = "Versión de la app",
                    description = "1.0.0",
                    icon = Icons.Default.Info,
                    type = SettingType.Info("1.0.0")
                ),
                SettingOption(
                    title = "Calificar app",
                    icon = Icons.Default.Star,
                    type = SettingType.Navigation("rate")
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            settingsGroups.forEach { (groupTitle, options) ->
                SettingsGroup(
                    title = groupTitle,
                    options = options,
                    onToggleChange = { title, newValue ->
                        when (title) {
                            "Notificaciones push" -> notificationsEnabled = newValue
                            "Modo oscuro" -> darkModeEnabled = newValue
                            "Ubicación" -> locationEnabled = newValue
                            "Marketing por email" -> emailMarketingEnabled = newValue
                        }
                    },
                    onNavigationClick = { route ->
                        // TODO: Handle navigation
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Settings group
 */
@Composable
fun SettingsGroup(
    title: String,
    options: List<SettingOption>,
    onToggleChange: (String, Boolean) -> Unit,
    onNavigationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                options.forEachIndexed { index, option ->
                    SettingOptionItem(
                        option = option,
                        onToggleChange = { onToggleChange(option.title, it) },
                        onNavigationClick = { onNavigationClick(it) },
                        showDivider = index < options.size - 1
                    )
                }
            }
        }
    }
}

/**
 * Setting option item
 */
@Composable
fun SettingOptionItem(
    option: SettingOption,
    onToggleChange: (Boolean) -> Unit,
    onNavigationClick: (String) -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                option.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                option.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when (option.type) {
                is SettingType.Toggle -> {
                    Switch(
                        checked = (option.type as SettingType.Toggle).initialValue,
                        onCheckedChange = onToggleChange,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = EnergyGreen,
                            checkedThumbColor = White
                        )
                    )
                }
                is SettingType.Navigation -> {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                is SettingType.Info -> {
                    Text(
                        text = (option.type as SettingType.Info).value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}
