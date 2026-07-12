package com.aethersentinel.showcase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aethersentinel.core.OkAether
import com.aethersentinel.showcase.data.AetherRepository
import com.aethersentinel.showcase.ui.components.TerminalLogView
import com.aethersentinel.showcase.ui.navigation.AppDestination
import com.aethersentinel.showcase.ui.screens.DeveloperSandboxScreen
import com.aethersentinel.showcase.ui.screens.LedgerStatusScreen
import com.aethersentinel.showcase.ui.screens.PlaygroundScreen
import com.aethersentinel.showcase.ui.theme.OkAetherShowcaseTheme
import com.aethersentinel.showcase.ui.theme.CanvasSlate
import com.aethersentinel.showcase.ui.theme.CardSlate
import com.aethersentinel.showcase.ui.theme.EmeraldGreen
import com.aethersentinel.showcase.ui.theme.NeonCyan
import com.aethersentinel.showcase.ui.theme.TextMuted
import com.aethersentinel.showcase.ui.theme.TextPrimary
import com.aethersentinel.showcase.viewmodel.AetherViewModelFactory
import com.aethersentinel.showcase.viewmodel.AppEventsViewModel
import com.aethersentinel.showcase.viewmodel.LedgerViewModel
import com.aethersentinel.showcase.viewmodel.PlaygroundViewModel
import com.aethersentinel.showcase.viewmodel.SandboxViewModel
import com.aethersentinel.showcase.viewmodel.TerminalLogViewModel

/**
 * NOTE ON NAMING:
 * The [android.app.Application] subclass is [OkAetherApp] (OkAetherApp.kt).
 * The root Composable below is deliberately named [OkAetherShowcaseRoot] — NOT
 * `OkAetherApp()` — to prevent any class/function naming collision between
 * the Application class and a top-level Composable function.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OkAetherShowcaseTheme {
                OkAetherShowcaseRoot()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        OkAether.setAppInForeground(true)
    }

    override fun onPause() {
        super.onPause()
        OkAether.setAppInForeground(false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OkAetherShowcaseRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Shared repository + cross-tab ViewModels so every SDK interaction,
    // regardless of which tab triggered it, flows through one adapter and
    // streams into one terminal console.
    val repository = remember { AetherRepository(context.applicationContext) }
    val terminalViewModel: TerminalLogViewModel = viewModel()
    val eventsViewModel: AppEventsViewModel = viewModel()
    val vmFactory = remember(repository, terminalViewModel, eventsViewModel) {
        AetherViewModelFactory(repository, terminalViewModel, eventsViewModel)
    }
    val playgroundViewModel: PlaygroundViewModel = viewModel(factory = vmFactory)
    val sandboxViewModel: SandboxViewModel = viewModel(factory = vmFactory)
    val ledgerViewModel: LedgerViewModel = viewModel(factory = vmFactory)

    val logLines by terminalViewModel.lines.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(eventsViewModel) {
        eventsViewModel.snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = CanvasSlate,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OkAether",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, APP_SHARE_MESSAGE)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share OkAether"))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share OkAether",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasSlate)
            )
        },
        bottomBar = {
            OkAetherBottomNavigation(navController)
        },
        floatingActionButton = {
            PulsingGitHubFab(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPO_URL))
                    context.startActivity(intent)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = CardSlate,
                    contentColor = EmeraldGreen,
                    snackbarData = data
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CanvasSlate)
        ) {
            NavHost(
                navController = navController,
                startDestination = AppDestination.Playground.route,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                composable(AppDestination.Playground.route) {
                    PlaygroundScreen(viewModel = playgroundViewModel)
                }
                composable(AppDestination.Sandbox.route) {
                    DeveloperSandboxScreen(viewModel = sandboxViewModel)
                }
                composable(AppDestination.Ledger.route) {
                    LedgerStatusScreen(viewModel = ledgerViewModel)
                }
            }

            // Terminal sits OUTSIDE NavHost so it persists across all three tabs.
            TerminalLogView(lines = logLines)
        }
    }
}

@Composable
private fun PulsingGitHubFab(onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "fab-pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab-scale"
    )
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = EmeraldGreen,
        contentColor = Color.Black,
        icon = { Icon(Icons.Filled.Bolt, contentDescription = null) },
        text = { Text("Star on GitHub", fontWeight = FontWeight.SemiBold) },
        modifier = Modifier.scale(scale)
    )
}

@Composable
private fun OkAetherBottomNavigation(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = CanvasSlate) {
        AppDestination.all.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
