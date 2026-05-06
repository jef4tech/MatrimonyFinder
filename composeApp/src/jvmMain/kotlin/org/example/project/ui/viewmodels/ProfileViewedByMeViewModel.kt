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

data class ProfileViewedByMeState(
    val views: List<ProfileViewItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface ProfileViewedByMeEvent {
    data class LoadViews(val clientId: String, val token: String) : ProfileViewedByMeEvent
}

class ProfileViewedByMeViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileViewedByMeState())
    val state: StateFlow<ProfileViewedByMeState> = _state.asStateFlow()

    fun onEvent(event: ProfileViewedByMeEvent) {
        when (event) {
            is ProfileViewedByMeEvent.LoadViews -> loadViews(event.clientId, event.token)
        }
    }

    private fun loadViews(clientId: String, token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val request = ProfileViewsRequest(
                pagination = Pagination(1, 500),
                filters = ProfileViewsFilters(
                    fromTimeStamp = "2023-01-01T00:00:00.000Z",
                    toTimeStamp = "2026-01-01T00:00:00.000Z",
                    availableProfilesOnly = true
                )
            )
            val result = repository.getProfileViewedByMe(clientId, token, request)
            if (result.isSuccess) {
                val items = result.getOrNull()?.items ?: emptyList()
                _state.update { it.copy(isLoading = false, views = items) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Failed to load views") }
            }
        }
    }
}

