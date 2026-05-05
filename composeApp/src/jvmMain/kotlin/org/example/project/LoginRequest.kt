package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequest(
    @SerialName("userId") val userId: String,
    @SerialName("password") val password: String,
    @SerialName("deviceName") val deviceName: String = "desktop",
    @SerialName("osName") val osName: String = "Windows",
    @SerialName("browsingAppName") val browsingAppName: String = "Chrome - V145.0.0.0",
    @SerialName("latitude") val latitude: String = "",
    @SerialName("longitude") val longitude: String = "",
    @SerialName("ipAddress") val ipAddress: String = "",
    @SerialName("networkProviderName") val networkProviderName: String = "",
    @SerialName("firebaseToken") val firebaseToken: String? = null,
    @SerialName("buildVersion") val buildVersion: String = "V2.0.75",
    @SerialName("location") val location: String? = null
)
