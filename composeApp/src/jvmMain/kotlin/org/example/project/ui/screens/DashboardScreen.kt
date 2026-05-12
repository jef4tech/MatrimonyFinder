package org.example.project.ui.screens
import org.example.project.data.remote.models.*
import org.example.project.ui.viewmodels.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import org.example.project.data.remote.models.DashboardInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMyMatches: () -> Unit,
    onNavigateToWhoViewedMe: () -> Unit,
    onNavigateToProfileViewedByMe: () -> Unit,
    onNavigateToMutualMatches: () -> Unit,
    onNavigateToNewlyJoined: () -> Unit,
    onNavigateToContactViews: () -> Unit,
    onNavigateToContactsViewedByMe: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    DashboardScreenContent(
        infoList = state.dashboardInfo ?: emptyList(),
        onNavigateToMyMatches = onNavigateToMyMatches,
        onNavigateToWhoViewedMe = onNavigateToWhoViewedMe,
        onNavigateToProfileViewedByMe = onNavigateToProfileViewedByMe,
        onNavigateToMutualMatches = onNavigateToMutualMatches,
        onNavigateToNewlyJoined = onNavigateToNewlyJoined,
        onNavigateToContactViews = onNavigateToContactViews,
        onNavigateToContactsViewedByMe = onNavigateToContactsViewedByMe,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    infoList: List<DashboardInfo>,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToWhoViewedMe: () -> Unit,
    onNavigateToProfileViewedByMe: () -> Unit,
    onNavigateToMutualMatches: () -> Unit,
    onNavigateToNewlyJoined: () -> Unit,
    onNavigateToContactViews: () -> Unit,
    onNavigateToContactsViewedByMe: () -> Unit,
    onLogout: () -> Unit
) {
    fun getCount(name: String): Int? = infoList.find { it.name == name }?.count

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                infoList.map {
                    DashboardCard(
                        title = it.name ?: "Unknown",
                        count = it.count ?: 0,
                        modifier = Modifier.weight(1f).height(100.dp).padding(8.dp),
                        onClick = {}
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "My Matches",
                    count = getCount("My Matches"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToMyMatches
                )
                DashboardCard(
                    title = "Who Viewed Me",
                    count = getCount("Profile Views"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToWhoViewedMe
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Mutual Matches",
                    count = getCount("Mutual Matches"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToMutualMatches
                )
                DashboardCard(
                    title = "Newly Joined Profiles",
                    count = getCount("Newly Joined"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToNewlyJoined
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Profiles Viewed By Me",
                    count = getCount("Profiles Viewed By Me"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToProfileViewedByMe
                )
                DashboardCard(
                    title = "Contact Views",
                    count = getCount("Contact Views"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToContactViews
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Contacts Viewed By Me",
                    count = getCount("Contacts Viewed By Me"),
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToContactsViewedByMe
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(title: String, count: Int? = null, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (count != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreenContent(
            infoList = listOf(
                DashboardInfo(name = "Profile Views", count = 1057),
                DashboardInfo(name = "Contact Views", count = 13)
            ),
            onNavigateToMyMatches = {},
            onNavigateToWhoViewedMe = {},
            onNavigateToProfileViewedByMe = {},
            onNavigateToMutualMatches = {},
            onNavigateToNewlyJoined = {},
            onNavigateToContactViews = {},
            onNavigateToContactsViewedByMe = {},
            onLogout = {}
        )
    }
}

