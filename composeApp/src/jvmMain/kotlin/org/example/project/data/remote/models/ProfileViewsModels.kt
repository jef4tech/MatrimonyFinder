package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileViewsRequest(
    val pagination: Pagination,
    val filters: ProfileViewsFilters
)

@Serializable
data class ProfileViewsFilters(
    val fromTimeStamp: String,
    val toTimeStamp: String,
    val availableProfilesOnly: Boolean
)

@Serializable
data class ProfileViewsResponse(
    val totalCount: Int? = null,
    val pageSize: Int? = null,
    val currentPage: Int? = null,
    val items: List<ProfileViewItem>? = null
)

@Serializable
data class ProfileViewItem(
    val profileViewId: Long? = null,
    val candidate: CandidateProfile? = null,
    val isReaded: Boolean? = null,
    val activityLog: List<ActivityLog>? = null
)

@Serializable
data class ActivityLog(
    val activity: String? = null,
    val activityTimeStamp: String? = null,
    val activityStamp: Long? = null,
    val activityBy: String? = null
)
