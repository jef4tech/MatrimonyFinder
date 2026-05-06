package org.example.project.ui.screens
import org.example.project.data.remote.models.*
import org.example.project.ui.viewmodels.*

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewlyJoinedScreen(
    viewModel: NewlyJoinedViewModel = koinViewModel(),
    token: String,
    candidateId: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(NewlyJoinedEvent.LoadMatches(candidateId, token))
    }

    MyMatchesScreenContent(
        title = "Newly Joined Profiles",
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        matches = state.matches,
        onLogout = onLogout,
        onBack = onBack
    )
}

