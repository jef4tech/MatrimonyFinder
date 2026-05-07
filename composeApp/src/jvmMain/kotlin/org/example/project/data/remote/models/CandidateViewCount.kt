package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class CandidateViewCount(
    val name: String? = null,
    val code: String? = null,
    val totalCount: Int? = null,
    val unread: Int? = null,
    val unreadCount: Int? = null
)
