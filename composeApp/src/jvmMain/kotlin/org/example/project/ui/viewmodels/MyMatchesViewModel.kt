package org.example.project.ui.viewmodels
import org.example.project.data.remote.models.*
import org.example.project.data.repository.MatrimonyRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyMatchesState(
    val matches: List<MatchItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalRecords: Int = 0,
    val hasMore: Boolean = true
)

sealed interface MyMatchesEvent {
    data class LoadMatches(val candidateId: String, val token: String) : MyMatchesEvent
    data class LoadMore(val candidateId: String, val token: String) : MyMatchesEvent
}

class MyMatchesViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyMatchesState())
    val state: StateFlow<MyMatchesState> = _state.asStateFlow()

    fun onEvent(event: MyMatchesEvent) {
        when (event) {
            is MyMatchesEvent.LoadMatches -> loadMatches(event.candidateId, event.token)
            is MyMatchesEvent.LoadMore -> loadMore(event.candidateId, event.token)
        }
    }

    private fun loadMatches(candidateId: String, token: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    errorMessage = null,
                    matches = emptyList(),
                    currentPage = 0,
                    totalRecords = 0,
                    hasMore = true
                )
            }
            fetchPage(candidateId, token, page = 1, append = false)
        }
    }

    private fun loadMore(candidateId: String, token: String) {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }
            fetchPage(candidateId, token, page = current.currentPage + 1, append = true)
        }
    }

    private suspend fun fetchPage(candidateId: String, token: String, page: Int, append: Boolean) {
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
            pagination = Pagination(page, PAGE_SIZE)
        )
        val result = repository.getMyMatches(candidateId, token, request)
        if (result.isSuccess) {
            val response = result.getOrNull()
            val newItems = response?.items ?: emptyList()
            val total = response?.totalRecords ?: 0
            _state.update { prev ->
                val combined = if (append) prev.matches + newItems else newItems
                prev.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    matches = combined,
                    currentPage = page,
                    totalRecords = total,
                    hasMore = newItems.isNotEmpty() && combined.size < total
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load matches"
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

