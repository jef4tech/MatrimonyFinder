package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun MyMatchesScreen(
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var matches by remember { mutableStateOf<List<MatchItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val request = MyMatchesRequest(
            sort = listOf(SortOption("subscriptionStart", "Date Joined", 1, true)),
            filters = MatchFilters(
                profileOptions = ProfileOptions(
                    isAlreadySeen = true,
                    isAlreadyContacted = true,
                    isInterestSent = true,
                    isShortListed = true,
                    isWithPhoto = false,
                    isOnline = false,
                    isPremium = false
                )
            ),
            pagination = Pagination(1, 500)
        )
        val result = ApiClient.getMyMatches(candidateId, token, request)
        if (result.isSuccess) {
            matches = result.getOrNull()?.items ?: emptyList()
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Failed to load matches"
        }
        isLoading = false
    }

    MyMatchesScreenContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        matches = matches,
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matches) { match ->
                        MatchCard(match)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchItem) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        val photoUrl = match.photos?.candidatePhotos?.firstOrNull()?.displayPhotoUrl
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
}

@Composable
fun AsyncImage(url: String, modifier: Modifier = Modifier) {
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
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = modifier.background(Color.LightGray))
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
