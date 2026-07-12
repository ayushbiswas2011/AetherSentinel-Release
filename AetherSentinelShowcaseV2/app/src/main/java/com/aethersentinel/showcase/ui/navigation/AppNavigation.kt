package com.aethersentinel.showcase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Playground : AppDestination("playground", "Playground", Icons.Filled.Dashboard)
    data object Sandbox : AppDestination("sandbox", "Sandbox", Icons.Filled.Code)
    data object Ledger : AppDestination("ledger", "Ledger", Icons.Filled.Dns)

    companion object {
        val all = listOf(Playground, Sandbox, Ledger)
    }
}
