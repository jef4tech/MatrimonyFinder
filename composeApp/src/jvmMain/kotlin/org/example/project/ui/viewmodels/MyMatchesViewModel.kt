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
    val errorMessage: String? = null
)

sealed interface MyMatchesEvent {
    data class LoadMatches(val candidateId: String, val token: String) : MyMatchesEvent
}

class MyMatchesViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyMatchesState())
    val state: StateFlow<MyMatchesState> = _state.asStateFlow()

    fun onEvent(event: MyMatchesEvent) {
        when (event) {
            is MyMatchesEvent.LoadMatches -> loadMatches(event.candidateId, event.token)
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
            val result = repository.getMyMatches(candidateId, token, request)
            if (result.isSuccess) {
                val items = result.getOrNull()?.items ?: emptyList()
                _state.update { it.copy(isLoading = false, matches = items) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Failed to load matches") }
            }
        }
    }
}

