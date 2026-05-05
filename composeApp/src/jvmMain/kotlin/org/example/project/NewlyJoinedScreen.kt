package org.example.project

import androidx.compose.runtime.*

@Composable
fun NewlyJoinedScreen(
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var matches by remember { mutableStateOf<List<MatchItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val request = NewlyJoinedRequest(
            filters = NewlyJoinedFilters(
                moreCriteria = MoreCriteria(),
                preference = Preference(),
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
            pagination = Pagination(1, 500),
            sort = listOf(SortOption("loginDate", "Last Login Date", 2, true)),
            handleDuplicationByLogin = java.time.Instant.now().toString()
        )
        val result = ApiClient.getNewlyJoinedProfiles(candidateId, token, request)
        if (result.isSuccess) {
            matches = result.getOrNull()?.items ?: emptyList()
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Failed to load newly joined profiles"
        }
        isLoading = false
    }

    MyMatchesScreenContent(
        title = "Newly Joined Profiles",
        isLoading = isLoading,
        errorMessage = errorMessage,
        matches = matches,
        onLogout = onLogout,
        onBack = onBack
    )
}
