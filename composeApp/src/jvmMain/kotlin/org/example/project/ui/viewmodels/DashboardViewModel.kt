package org.example.project.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.remote.models.DashboardInfo
import org.example.project.data.repository.MatrimonyRepository

data class DashboardState(
    val isLoading: Boolean = false,
    val dashboardInfo: List<DashboardInfo>? = null,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun loadDashboardData() {
        if (_state.value.dashboardInfo != null) return // Already loaded

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.getDashboardData()
            result.fold(
                onSuccess = { response ->
                    _state.update { it.copy(isLoading = false, dashboardInfo = response.responsesInfo) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
