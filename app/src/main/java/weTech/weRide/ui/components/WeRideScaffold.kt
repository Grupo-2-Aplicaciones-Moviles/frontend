package weTech.weRide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import weTech.weRide.ui.theme.EnergyGreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * WeRide Scaffold
 * Standard app scaffold with optional top bar
 */
@Composable
fun WeRideScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { topBar?.invoke() },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = { floatingActionButton?.invoke() },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        content = content
    )
}

/**
 * WeRide Top App Bar
 * Standard top app bar with optional back button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeRideTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        navigationIcon = { navigationIcon?.invoke() },
        actions = {
            Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                actions()
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}

/**
 * WeRide Back Top App Bar
 * Top app bar with back button
 */
@Composable
fun WeRideBackTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    WeRideTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = contentColor
                )
            }
        },
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

/**
 * WeRide Simple Top Bar
 * Simplified top bar without back button
 */
@Composable
fun WeRideSimpleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    WeRideTopAppBar(
        title = title,
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Preview(showBackground = true)
@Composable
fun WeRideTopAppBarPreview() {
    WeRideTheme {
        Column {
            WeRideTopAppBar(
                title = "WeRide"
            )
            androidx.compose.material3.Text(
                text = "Content",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideBackTopAppBarPreview() {
    WeRideTheme {
        Column {
            WeRideBackTopAppBar(
                title = "Vehicle Details",
                onBackClick = { }
            )
            androidx.compose.material3.Text(
                text = "Content",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeRideScaffoldPreview() {
    WeRideTheme {
        WeRideScaffold(
            topBar = {
                WeRideTopAppBar(title = "WeRide")
            }
        ) { paddingValues ->
            androidx.compose.material3.Text(
                text = "Content",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
