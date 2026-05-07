package org.example.project.ui.screens
import org.example.project.data.remote.models.*
import org.example.project.ui.viewmodels.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun MyMatchesScreen(
    viewModel: MyMatchesViewModel = koinViewModel(),
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MyMatchesEvent.LoadMatches(candidateId, token))
    }

    MyMatchesScreenContent(
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        matches = state.matches,
        isLoadingMore = state.isLoadingMore,
        hasMore = state.hasMore,
        onLoadMore = { viewModel.onEvent(MyMatchesEvent.LoadMore(candidateId, token)) },
        onLogout = onLogout,
        onBack = onBack
    )
}

@Composable
fun MyMatchesScreenContent(
    title: String = "My Matches",
    isLoading: Boolean,
    errorMessage: String?,
    matches: List<MatchItem>?,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(title) },
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
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (matches.isNullOrEmpty()) {
                Text(
                    text = "No matches found.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val gridState = rememberLazyGridState()
                val shouldLoadMore by remember(hasMore, isLoadingMore) {
                    derivedStateOf {
                        val info = gridState.layoutInfo
                        val total = info.totalItemsCount
                        val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
                        total > 0 && lastVisible >= total - 4
                    }
                }
                LaunchedEffect(shouldLoadMore, hasMore, isLoadingMore) {
                    if (shouldLoadMore && hasMore && !isLoadingMore) {
                        onLoadMore()
                    }
                }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matches) { match ->
                        MatchCard(match)
                    }
                    if (isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchItem) {
    var showDialog by remember { mutableStateOf(false) }
    val photoUrl = match.photos?.candidatePhotos?.firstOrNull()?.displayPhotoUrl

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
                modifier = Modifier.fillMaxSize().background(Color.LightGray)
            )
        }
    }

    if (showDialog && !photoUrl.isNullOrEmpty()) {
        val details = CandidateDetails(
            profileId = match.profileId ?: "Unknown",
            age = match.age,
            height = match.heightInCentimeter,
            education = match.educationDetails,
            profession = match.profession?.details,
            location = listOfNotNull(match.workingState, match.workingCountry).joinToString(", ").takeIf { it.isNotBlank() },
            isPremium = match.isPremium ?: false,
            message = match.messageStatus?.message
        )
        FullScreenImageDialog(
            url = photoUrl,
            details = details,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun AsyncImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val bytes = java.net.URL(url).readBytes()
                val skiaImage = SkiaImage.makeFromEncoded(bytes)
                imageBitmap = skiaImage.toComposeImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = "Profile Photo",
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(modifier = modifier.background(Color.LightGray))
    }
}

data class CandidateDetails(
    val profileId: String,
    val age: Int?,
    val height: Int?,
    val education: String?,
    val profession: String?,
    val location: String?,
    val isPremium: Boolean,
    val message: String? = null,
    val activityLog: List<ActivityEntry>? = null
)

data class ActivityEntry(
    val activity: String,
    val timestamp: String? = null,
    val by: String? = null
)

@Composable
fun FullScreenImageDialog(url: String, details: CandidateDetails?, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Side: Image (75%)
                Box(
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        url = url,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Right Side: Details (25%)
                if (details != null) {
                    Box(
                        modifier = Modifier
                            .weight(0.25f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(24.dp)
                            .clickable(enabled = false) {} // Prevent dismiss when clicking details
                    ) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                text = "Profile Details",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "ID: ${details.profileId}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                if (details.isPremium) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("Premium", color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Age: ${details.age ?: "N/A"} yrs", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Height: ${details.height ?: "N/A"} cm", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Education: ${details.education ?: "N/A"}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Profession: ${details.profession ?: "N/A"}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Location: ${details.location ?: "N/A"}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            if (!details.message.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Message: ${details.message}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            }
                            if (!details.activityLog.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(20.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Activity",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                details.activityLog.forEach { entry ->
                                    Text(
                                        text = entry.activity,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val sub = listOfNotNull(entry.timestamp, entry.by).joinToString(" • ")
                                    if (sub.isNotBlank()) {
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Fill space if no details
                    Spacer(modifier = Modifier.weight(0.25f))
                }
            }
        }
    }
}

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
fun MyMatchesScreenPreview() {
    MaterialTheme {
        MyMatchesScreenContent(
            isLoading = false,
            errorMessage = null,
            matches = listOf(
                MatchItem(
                    candidateId = "123",
                    profileId = "jeffjhin",
                    age = 28,
                    heightInCentimeter = 170,
                    isPremium = true,
                    educationDetails = "B.Tech",
                    profession = Profession("Software Engineer", "Tech Corp"),
                    workingState = "Kerala",
                    workingCountry = "India"
                ),
                MatchItem(
                    candidateId = "124",
                    profileId = "CHV67890",
                    age = 25,
                    heightInCentimeter = 162,
                    isPremium = false,
                    educationDetails = "MBBS",
                    profession = Profession("Doctor", "City Hospital"),
                    workingState = "Karnataka",
                    workingCountry = "India"
                )
            ),
            onLogout = {},
            onBack = {}
        )
    }
}

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
fun FullScreenImageDialogPreview() {
    MaterialTheme {
        FullScreenImageDialog(
            url = "https://via.placeholder.com/400",
            details = CandidateDetails(
                profileId = "CHV12345",
                age = 28,
                height = 175,
                education = "B.Tech",
                profession = "Software Engineer",
                location = "Kerala, India",
                isPremium = true
            ),
            onDismiss = {}
        )
    }
}

