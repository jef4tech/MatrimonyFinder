package org.example.project

import androidx.compose.runtime.*

@Composable
fun MutualMatchesScreen(
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var matches by remember { mutableStateOf<List<MatchItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
        val result = ApiClient.getMutualMatches(candidateId, token, request)
        if (result.isSuccess) {
            matches = result.getOrNull()?.items ?: emptyList()
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Failed to load mutual matches"
        }
        isLoading = false
    }

    MyMatchesScreenContent(
        title = "Mutual Matches",
        isLoading = isLoading,
        errorMessage = errorMessage,
        matches = matches,
        onLogout = onLogout,
        onBack = onBack
    )
}
