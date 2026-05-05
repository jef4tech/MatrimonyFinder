package org.example.project

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
    val candidate: ViewedCandidate? = null,
    val isReaded: Boolean? = null,
    val activityLog: List<ActivityLog>? = null
)

@Serializable
data class ViewedCandidate(
    val candidateId: String? = null,
    val profileId: String? = null,
    val age: Int? = null,
    val photo: Photos? = null,
    val heightInCentimeter: Int? = null,
    val genderCode: String? = null,
    val complexion: String? = null,
    val maritalStatus: String? = null,
    val religion: String? = null,
    val educationDetails: String? = null,
    val profession: Profession? = null,
    val isOnline: Boolean? = null,
    val isPremium: Boolean? = null,
    val branch: String? = null
)

@Serializable
data class ActivityLog(
    val activity: String? = null,
    val activityTimeStamp: String? = null,
    val activityStamp: Long? = null,
    val activityBy: String? = null
)
