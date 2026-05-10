package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class MyMatchesRequest(
    val sort: List<SortOption>? = null,
    val filters: MatchFilters? = null,
    val pagination: Pagination? = null,
    val handleDuplicationByLogin: String? = null
)

@Serializable
data class MatchFilters(
    val profileOptions: ProfileOptions? = null,
    val searchByDate: String? = null
)
