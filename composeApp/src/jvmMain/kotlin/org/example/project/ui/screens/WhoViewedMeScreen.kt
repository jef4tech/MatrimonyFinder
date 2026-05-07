package org.example.project.ui.screens
import org.example.project.data.remote.models.*
import org.example.project.ui.viewmodels.*

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhoViewedMeScreen(
    viewModel: WhoViewedMeViewModel = koinViewModel(),
    token: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(WhoViewedMeEvent.LoadViews(token))
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
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.views.isEmpty()) {
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
                    items(state.views) { view ->
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
        val details = view.candidate.let { candidate ->
            CandidateDetails(
                profileId = candidate.profileId ?: "Unknown",
                age = candidate.age,
                height = candidate.heightInCentimeter,
                education = candidate.educationDetails,
                profession = listOfNotNull(
                    candidate.profession?.professionName,
                    candidate.profession?.name,
                    candidate.profession?.details,
                    candidate.profession?.organization,
                    candidate.profession?.workingPlace
                ).filter { it.isNotBlank() }.joinToString(" • ").takeIf { it.isNotBlank() },
                location = candidate.branch,
                isPremium = candidate.isPremium ?: false,
                message = candidate.messageStatus?.message,
                activityLog = view.activityLog
                    ?.mapNotNull { log ->
                        val text = log.activity?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        ActivityEntry(
                            activity = text,
                            timestamp = formatActivityTimestamp(log.activityTimeStamp),
                            by = log.activityBy?.takeIf { it.isNotBlank() }
                        )
                    }
                    ?.takeIf { it.isNotEmpty() }
            )
        }
        FullScreenImageDialog(
            url = photoUrl,
            details = details,
            onDismiss = { showDialog = false }
        )
    }
}

private val activityDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)

private fun formatActivityTimestamp(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return try {
        Instant.parse(raw).atZone(ZoneId.systemDefault()).format(activityDateFormatter)
    } catch (e: Exception) {
        raw
    }
}


