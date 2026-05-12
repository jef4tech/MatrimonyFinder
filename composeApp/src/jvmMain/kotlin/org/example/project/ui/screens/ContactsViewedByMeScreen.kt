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
            PremiumTopAppBar(
                title = "Contacts Viewed By Me",
                onBack = onBack,
                onLogout = onLogout
            )
        },
        containerColor = BaseBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(BaseBlack)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ChampagneGold)
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.views.isEmpty()) {
                Text(
                    text = "You haven't viewed any contact details yet.",
                    style = BodyLg,
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
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalArrangement = Arrangement.spacedBy(48.dp),
                    modifier = Modifier.fillMaxSize()
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
                                CircularProgressIndicator(color = ChampagneGold)
                            }
                        }
                    }
                }
            }
        }
    }
}
