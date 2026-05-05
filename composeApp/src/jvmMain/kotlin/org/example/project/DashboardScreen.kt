package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMyMatches: () -> Unit,
    onNavigateToWhoViewedMe: () -> Unit,
    onNavigateToProfileViewedByMe: () -> Unit,
    onNavigateToMutualMatches: () -> Unit,
    onNavigateToNewlyJoined: () -> Unit,
    onLogout: () -> Unit
) {
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "My Matches",
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToMyMatches
                )
                DashboardCard(
                    title = "Who Viewed Me",
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToWhoViewedMe
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Mutual Matches",
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToMutualMatches
                )
                DashboardCard(
                    title = "Newly Joined Profiles",
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToNewlyJoined
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    title = "Viewed By Me",
                    modifier = Modifier.weight(1f).height(150.dp),
                    onClick = onNavigateToProfileViewedByMe
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen(
            onNavigateToMyMatches = {},
            onNavigateToWhoViewedMe = {},
            onNavigateToProfileViewedByMe = {},
            onNavigateToMutualMatches = {},
            onNavigateToNewlyJoined = {},
            onLogout = {}
        )
    }
}