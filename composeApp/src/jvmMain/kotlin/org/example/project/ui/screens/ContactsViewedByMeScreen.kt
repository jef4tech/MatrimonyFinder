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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsViewedByMeScreen(
    viewModel: ContactsViewedByMeViewModel = koinViewModel(),
    token: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ContactsViewedByMeEvent.LoadViews(token))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts Viewed By Me") },
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
                    text = "You haven't viewed any contacts yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val gridState = rememberLazyGridState()
                val isLoadingMore = state.isLoadingMore
                val hasMore = state.hasMore
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
                        viewModel.onEvent(ContactsViewedByMeEvent.LoadMore(token))
                    }
                }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.views) { view ->
                        WhoViewedCard(view)
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
