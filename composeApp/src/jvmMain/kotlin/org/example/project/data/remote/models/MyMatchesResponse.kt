package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class MyMatchesResponse(
    val items: List<CandidateProfile>? = null,
    val totalRecords: Int? = null
)
