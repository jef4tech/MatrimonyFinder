package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val responsesInfo: List<DashboardInfo>? = null
)

@Serializable
data class DashboardInfo(
    val displayOrder: Int? = null,
    val name: String? = null,
    val count: Int? = null,
    val typeCode: String? = null,
    val groupCode: String? = null
)
