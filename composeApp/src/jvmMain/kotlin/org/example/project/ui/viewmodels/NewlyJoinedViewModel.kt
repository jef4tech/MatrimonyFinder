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

data class NewlyJoinedState(
    val matches: List<MatchItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface NewlyJoinedEvent {
    data class LoadMatches(val candidateId: String, val token: String) : NewlyJoinedEvent
}

class NewlyJoinedViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewlyJoinedState())
    val state: StateFlow<NewlyJoinedState> = _state.asStateFlow()

    fun onEvent(event: NewlyJoinedEvent) {
        when (event) {
            is NewlyJoinedEvent.LoadMatches -> loadMatches(event.candidateId, event.token)
        }
    }

    private fun loadMatches(candidateId: String, token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
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
                pagination = Pagination(1, 1000),
                sort = listOf(SortOption("loginDate", "Last Login Date", 2, true)),
                handleDuplicationByLogin = java.time.Instant.now().toString()
            )
            val result = repository.getNewlyJoinedProfiles(candidateId, token, request)
            if (result.isSuccess) {
                val items = result.getOrNull()?.items ?: emptyList()
                _state.update { it.copy(isLoading = false, matches = items) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Failed to load newly joined profiles") }
            }
        }
    }
}

