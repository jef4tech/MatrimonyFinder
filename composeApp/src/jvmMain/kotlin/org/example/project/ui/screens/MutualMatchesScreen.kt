package org.example.project.ui.screens
import org.example.project.data.remote.models.*
import org.example.project.ui.viewmodels.*

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MutualMatchesScreen(
    viewModel: MutualMatchesViewModel = koinViewModel(),
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MutualMatchesEvent.LoadMatches(candidateId, token))
    }

    MyMatchesScreenContent(
        title = "Mutual Matches",
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        matches = state.matches,
        isLoadingMore = state.isLoadingMore,
        hasMore = state.hasMore,
        onLoadMore = { viewModel.onEvent(MutualMatchesEvent.LoadMore(candidateId, token)) },
        onLogout = onLogout,
        onBack = onBack
    )
}

