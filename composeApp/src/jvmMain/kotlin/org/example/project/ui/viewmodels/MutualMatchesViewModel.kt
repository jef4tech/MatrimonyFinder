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
    val matches: List<MatchItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface MutualMatchesEvent {
    data class LoadMatches(val candidateId: String, val token: String) : MutualMatchesEvent
}

class MutualMatchesViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MutualMatchesState())
    val state: StateFlow<MutualMatchesState> = _state.asStateFlow()

    fun onEvent(event: MutualMatchesEvent) {
        when (event) {
            is MutualMatchesEvent.LoadMatches -> loadMatches(event.candidateId, event.token)
        }
    }

    private fun loadMatches(candidateId: String, token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
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
            val result = repository.getMutualMatches(candidateId, token, request)
            if (result.isSuccess) {
                val items = result.getOrNull()?.items ?: emptyList()
                _state.update { it.copy(isLoading = false, matches = items) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Failed to load mutual matches") }
            }
        }
    }
}

