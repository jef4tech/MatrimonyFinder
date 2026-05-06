package org.example.project.data.remote.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class NewlyJoinedRequest(
    val filters: NewlyJoinedFilters,
    val pagination: Pagination,
    val sort: List<SortOption>,
    val handleDuplicationByLogin: String
)

@Serializable
data class NewlyJoinedFilters(
    val branchesId: List<Int> = emptyList(),
    val habits: JsonElement? = null,
    val moreCriteria: MoreCriteria,
    val preference: Preference,
    val profileOptions: ProfileOptions,
    val searchByDate: JsonElement? = null
)

@Serializable
data class MoreCriteria(
    val isSpecialNeed: Boolean = false,
    val disabilityId: List<Int> = emptyList(),
    val familyStatusId: List<Int> = emptyList(),
    val bodyTypeId: List<Int> = emptyList(),
    val complexionId: List<Int> = emptyList(),
    val incomeId: List<Int> = emptyList(),
    val organizationId: List<Int> = emptyList(),
    val creatorId: List<Int> = emptyList(),
    val residingCountryId: List<Int> = emptyList(),
    val residentTypeId: List<Int> = emptyList()
)

@Serializable
data class Preference(
    val minAge: Int = 18,
    val maxAge: Int = 50,
    val minHeight: Int = 120,
    val maxHeight: Int = 220,
    val denominationId: List<Int> = emptyList(),
    val maritalStatusId: List<Int> = emptyList(),
    val isChildren: JsonElement? = null,
    val occupationId: List<Int> = emptyList(),
    val educationId: List<Int> = emptyList(),
    val workingCountryId: List<Int> = emptyList(),
    val workingStateId: List<Int> = emptyList(),
    val workingDistrictId: List<Int> = emptyList(),
    val nativeCountryId: List<Int> = emptyList(),
    val nativeStateId: List<Int> = emptyList(),
    val nativeDistrictId: List<Int> = emptyList()
)
