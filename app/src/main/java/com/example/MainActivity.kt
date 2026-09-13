package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BrandViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BrandBibleApp()
            }
        }
    }
}

enum class AppDestination(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector) {
    GENERATOR("Generator", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome),
    DASHBOARD("Brand Bible", Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
    CHAT("Director AI", Icons.Outlined.Psychology, Icons.Filled.Psychology),
    LIBRARY("Library", Icons.Outlined.CollectionsBookmark, Icons.Filled.CollectionsBookmark)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandBibleApp(
    viewModel: BrandViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf(AppDestination.GENERATOR) }
    val snackbarHostState = remember { SnackbarHostState() }

    val activeBible by viewModel.activeBible.collectAsStateWithLifecycle()
    val savedBibles by viewModel.savedBibles.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val companyName by viewModel.companyName.collectAsStateWithLifecycle()
    val industry by viewModel.industry.collectAsStateWithLifecycle()
    val mission by viewModel.mission.collectAsStateWithLifecycle()
    val tone by viewModel.tone.collectAsStateWithLifecycle()

    val isGeneratingBible by viewModel.isGeneratingBible.collectAsStateWithLifecycle()
    val isPolishingMission by viewModel.isPolishingMission.collectAsStateWithLifecycle()
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsStateWithLifecycle()
    val isAuditing by viewModel.isAuditing.collectAsStateWithLifecycle()
    val isSearchingTrends by viewModel.isSearchingTrends.collectAsStateWithLifecycle()
    val isChatSending by viewModel.isChatSending.collectAsStateWithLifecycle()

    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    // Show Snackbars on state feedback
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearFeedback()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Brand Bible",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (activeBible.brandName.isNotBlank()) "Active: ${activeBible.brandName}" else "Identity Architect",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    if (currentDestination != AppDestination.DASHBOARD) {
                        TextButton(
                            onClick = { currentDestination = AppDestination.DASHBOARD },
                            modifier = Modifier.testTag("topbar_view_dashboard_button")
                        ) {
                            Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Bible", fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                AppDestination.values().forEach { dest ->
                    val isSelected = currentDestination == dest
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) dest.selectedIcon else dest.icon,
                                contentDescription = dest.title
                            )
                        },
                        label = { Text(dest.title, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.GENERATOR -> {
                    GeneratorFormView(
                        companyName = companyName,
                        industry = industry,
                        mission = mission,
                        tone = tone,
                        isPolishingMission = isPolishingMission,
                        isGeneratingBible = isGeneratingBible,
                        onCompanyNameChange = { viewModel.setCompanyName(it) },
                        onIndustryChange = { viewModel.setIndustry(it) },
                        onMissionChange = { viewModel.setMission(it) },
                        onToneChange = { viewModel.setTone(it) },
                        onPolishMission = { viewModel.polishMission() },
                        onGenerate = {
                            viewModel.generateBrandBible {
                                currentDestination = AppDestination.DASHBOARD
                            }
                        }
                    )
                }

                AppDestination.DASHBOARD -> {
                    BrandBibleDashboard(
                        bible = activeBible,
                        isGeneratingImage = isGeneratingImage,
                        isAuditing = isAuditing,
                        isSearchingTrends = isSearchingTrends,
                        onGenerateImage = { prompt, size -> viewModel.generateLogoImage(prompt, size) },
                        onRunDeepAudit = { viewModel.runDeepThinkingAudit() },
                        onRefreshSearchTrends = { viewModel.refreshSearchTrends() },
                        onSaveBible = { viewModel.saveCurrentBible() }
                    )
                }

                AppDestination.CHAT -> {
                    CreativeDirectorChatView(
                        messages = chatMessages,
                        isSending = isChatSending,
                        activeBrandBible = activeBible,
                        onSendMessage = { text, model -> viewModel.sendChatMessage(text, model) },
                        onClearChat = { viewModel.clearChat() }
                    )
                }

                AppDestination.LIBRARY -> {
                    SavedBrandsView(
                        savedBibles = savedBibles,
                        activeBibleId = activeBible.id,
                        onSelectBible = { bible ->
                            viewModel.selectBible(bible)
                            currentDestination = AppDestination.DASHBOARD
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onDeleteBible = { viewModel.deleteBible(it) }
                    )
                }
            }
        }
    }
}
