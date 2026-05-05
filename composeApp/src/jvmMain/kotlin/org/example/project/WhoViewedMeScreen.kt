package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhoViewedMeScreen(
    token: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var views by remember { mutableStateOf<List<ProfileViewItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val request = ProfileViewsRequest(
            pagination = Pagination(1, 500),
            filters = ProfileViewsFilters(
                fromTimeStamp = "2023-01-01T00:00:00.000Z",
                toTimeStamp = "2026-01-01T00:00:00.000Z",
                availableProfilesOnly = true
            )
        )
        val result = ApiClient.getProfileViews(token, request)
        if (result.isSuccess) {
            views = result.getOrNull()?.items ?: emptyList()
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Failed to load views"
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Who Viewed Me") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (views.isNullOrEmpty()) {
                Text(
                    text = "No one has viewed your profile yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(views!!) { view ->
                        WhoViewedCard(view)
                    }
                }
            }
        }
    }
}

@Composable
fun WhoViewedCard(view: ProfileViewItem) {
    var showDialog by remember { mutableStateOf(false) }
    val photoUrl = view.candidate?.photo?.candidatePhotos?.firstOrNull()?.displayPhotoUrl

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable {
            if (!photoUrl.isNullOrEmpty()) {
                showDialog = true
            }
        }
    ) {
        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                url = photoUrl,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("No Photo")
            }
        }
    }
    
    if (showDialog && !photoUrl.isNullOrEmpty()) {
        val details = view.candidate?.let { candidate ->
            CandidateDetails(
                profileId = candidate.profileId ?: "Unknown",
                age = candidate.age,
                height = candidate.heightInCentimeter,
                education = candidate.educationDetails,
                profession = candidate.profession?.details,
                location = candidate.branch,
                isPremium = candidate.isPremium ?: false
            )
        }
        FullScreenImageDialog(
            url = photoUrl,
            details = details,
            onDismiss = { showDialog = false }
        )
    }
}
