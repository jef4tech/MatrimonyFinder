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
data class SortOption(
    val typeCode: String,
    val name: String,
    val order: Int,
    val isSelected: Boolean
)

@Serializable
data class MatchFilters(
    val profileOptions: ProfileOptions? = null,
    val searchByDate: String? = null
)

@Serializable
data class ProfileOptions(
    val isAlreadySeen: Boolean,
    val isAlreadyContacted: Boolean,
    val isInterestSent: Boolean,
    val isShortListed: Boolean,
    val isWithPhoto: Boolean,
    val isOnline: Boolean,
    val isPremium: Boolean
)

@Serializable
data class Pagination(
    val currentPage: Int,
    val recordCount: Int
)
