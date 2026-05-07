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
import java.time.Instant
import java.time.ZoneOffset

data class WhoViewedMeState(
    val views: List<ProfileViewItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface WhoViewedMeEvent {
    data class LoadViews(val token: String) : WhoViewedMeEvent
}

class WhoViewedMeViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WhoViewedMeState())
    val state: StateFlow<WhoViewedMeState> = _state.asStateFlow()

    fun onEvent(event: WhoViewedMeEvent) {
        when (event) {
            is WhoViewedMeEvent.LoadViews -> loadViews(event.token)
        }
    }

    private fun loadViews(token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Instant.now()
            val threeYearsAgo = now.atZone(ZoneOffset.UTC).minusYears(3).toInstant()
            val request = ProfileViewsRequest(
                pagination = Pagination(1, 1000),
                filters = ProfileViewsFilters(
                    fromTimeStamp = threeYearsAgo.toString(),
                    toTimeStamp = now.toString(),
                    availableProfilesOnly = true
                )
            )
            val result = repository.getProfileViews(token, request)
            if (result.isSuccess) {
                val items = result.getOrNull()?.items ?: emptyList()
                _state.update { it.copy(isLoading = false, views = items) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Failed to load views") }
            }
        }
    }
}

