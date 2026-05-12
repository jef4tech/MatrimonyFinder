package org.example.project.ui.screens

import org.example.project.data.remote.models.CandidateProfile
import org.example.project.ui.viewmodels.DashboardViewModel
import org.example.project.ui.viewmodels.MutualMatchesEvent
import org.example.project.ui.viewmodels.MutualMatchesViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

// Theme tokens moved to SharedThemeComponents.kt

data class SidebarItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    token: String = "",
    candidateId: String = "",
    onNavigateToMyMatches: () -> Unit,
    onNavigateToWhoViewedMe: () -> Unit,
    onNavigateToProfileViewedByMe: () -> Unit,
    onNavigateToMutualMatches: () -> Unit,
    onNavigateToNewlyJoined: () -> Unit,
    onNavigateToContactViews: () -> Unit,
    onNavigateToContactsViewedByMe: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
    mutualMatchesViewModel: MutualMatchesViewModel = koinViewModel()
) {
    val mutualState by mutualMatchesViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(token, candidateId) {
        if (token.isNotEmpty() && candidateId.isNotEmpty()) {
            mutualMatchesViewModel.onEvent(MutualMatchesEvent.LoadMatches(candidateId, token))
        }
    }

    val sidebarItems = listOf(
        SidebarItem(Icons.Outlined.People, "My Matches", onNavigateToMyMatches),
        SidebarItem(Icons.Outlined.Handshake, "Mutual Matches", onNavigateToMutualMatches),
        SidebarItem(Icons.Outlined.PersonAdd, "Newly Joined", onNavigateToNewlyJoined),
        SidebarItem(Icons.Outlined.Visibility, "Who Viewed Me", onNavigateToWhoViewedMe),
        SidebarItem(Icons.Outlined.Search, "Viewed By Me", onNavigateToProfileViewedByMe),
        SidebarItem(Icons.Outlined.ContactPage, "Contact Views", onNavigateToContactViews),
        SidebarItem(Icons.Outlined.Contacts, "Contacts Viewed By Me", onNavigateToContactsViewedByMe)
    )

    DashboardScreenContent(
        matches = mutualState.matches,
        isLoading = mutualState.isLoading,
        sidebarItems = sidebarItems,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    matches: List<CandidateProfile>,
    isLoading: Boolean,
    sidebarItems: List<SidebarItem>,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBlack)
    ) {
        // Left Panel (Sidebar)
        NavigationRail(
            containerColor = SurfaceCharcoal,
            modifier = Modifier.width(80.dp).fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            sidebarItems.forEach { item ->
                NavigationRailItem(
                    selected = false,
                    onClick = item.onClick,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = OffWhite
                        )
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            NavigationRailItem(
                selected = false,
                onClick = onLogout,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFD4A5A5) // Dusty Rose accent for logout
                    )
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Main Content Area
        Scaffold(
            containerColor = BaseBlack,
            topBar = {
                TopAppBar(
                    title = { Text("Gallery", style = HeadlineLg) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BaseBlack.copy(alpha = 0.8f),
                        titleContentColor = OffWhite
                    )
                )
            },
            modifier = Modifier.weight(1f)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BaseBlack)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ChampagneGold
                    )
                } else if (matches.isEmpty()) {
                    Text(
                        text = "No profiles found in the gallery.",
                        style = BodyLg,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 250.dp),
                        contentPadding = PaddingValues(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalArrangement = Arrangement.spacedBy(48.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(matches) { match ->
                            GalleryProfileCard(match = match, onClick = { /* View profile details */ })
                        }
                    }
                }
            }
        }
    }
}

// GalleryProfileCard moved to SharedThemeComponents.kt

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreenContent(
            matches = emptyList(),
            isLoading = false,
            sidebarItems = emptyList(),
            onLogout = {}
        )
    }
}


