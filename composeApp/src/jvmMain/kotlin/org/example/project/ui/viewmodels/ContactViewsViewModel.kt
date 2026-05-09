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

data class ContactViewsState(
    val views: List<ProfileViewItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalRecords: Int = 0,
    val hasMore: Boolean = true
)

sealed interface ContactViewsEvent {
    data class LoadViews(val token: String) : ContactViewsEvent
    data class LoadMore(val token: String) : ContactViewsEvent
}

class ContactViewsViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ContactViewsState())
    val state: StateFlow<ContactViewsState> = _state.asStateFlow()

    fun onEvent(event: ContactViewsEvent) {
        when (event) {
            is ContactViewsEvent.LoadViews -> loadViews(event.token)
            is ContactViewsEvent.LoadMore -> loadMore(event.token)
        }
    }

    private fun loadViews(token: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    errorMessage = null,
                    views = emptyList(),
                    currentPage = 0,
                    totalRecords = 0,
                    hasMore = true
                )
            }
            fetchPage(token, page = 1, append = false)
        }
    }

    private fun loadMore(token: String) {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }
            fetchPage(token, page = current.currentPage + 1, append = true)
        }
    }

    private suspend fun fetchPage(token: String, page: Int, append: Boolean) {
        val now = Instant.now()
        val threeYearsAgo = now.atZone(ZoneOffset.UTC).minusYears(3).toInstant()
        val request = ProfileViewsRequest(
            pagination = Pagination(page, PAGE_SIZE),
            filters = ProfileViewsFilters(
                fromTimeStamp = threeYearsAgo.toString(),
                toTimeStamp = now.toString(),
                availableProfilesOnly = true
            )
        )
        val result = repository.getContactViews(token, request)
        if (result.isSuccess) {
            val response = result.getOrNull()
            val newItems = response?.items ?: emptyList()
            val total = response?.totalCount ?: 0
            _state.update { prev ->
                val combined = if (append) prev.views + newItems else newItems
                val moreRemaining =
                    if (total > 0) combined.size < total else newItems.size >= PAGE_SIZE
                prev.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    views = combined,
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
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load views"
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
