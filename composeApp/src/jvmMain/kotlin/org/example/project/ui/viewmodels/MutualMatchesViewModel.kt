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

data class MutualMatchesState(
    val matches: List<CandidateProfile> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalRecords: Int = 0,
    val hasMore: Boolean = true
)

sealed interface MutualMatchesEvent {
    data class LoadMatches(val candidateId: String, val token: String) : MutualMatchesEvent
    data class LoadMore(val candidateId: String, val token: String) : MutualMatchesEvent
}

class MutualMatchesViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MutualMatchesState())
    val state: StateFlow<MutualMatchesState> = _state.asStateFlow()

    fun onEvent(event: MutualMatchesEvent) {
        when (event) {
            is MutualMatchesEvent.LoadMatches -> loadMatches(event.candidateId, event.token)
            is MutualMatchesEvent.LoadMore -> loadMore(event.candidateId, event.token)
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
        val result = repository.getMutualMatches(candidateId, token, request)
        if (result.isSuccess) {
            val response = result.getOrNull()
            val newItems = response?.items ?: emptyList()
            val total = response?.totalRecords ?: 0
            _state.update { prev ->
                val combined = if (append) prev.matches + newItems else newItems
                val moreRemaining =
                    if (total > 0) combined.size < total else newItems.size >= PAGE_SIZE
                prev.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    matches = combined,
                    currentPage = page,
                    totalRecords = total,
                    hasMore = newItems.isNotEmpty() && moreRemaining
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load mutual matches"
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}

