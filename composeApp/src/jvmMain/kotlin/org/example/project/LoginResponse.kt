package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement

@Serializable
data class LoginResponse(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("token") val token: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("candidates") val candidates: List<Candidate>? = null,
    @SerialName("user") val user: JsonElement? = null,
    @SerialName("policyInfo") val policyInfo: JsonElement? = null,
    @SerialName("isHandled") val isHandled: Boolean? = null,
    @SerialName("id") val id: Int? = null
)

@Serializable
data class Candidate(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null
)
