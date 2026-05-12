package org.example.project.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.remote.models.LoginResponse
import org.example.project.data.repository.MatrimonyRepository
import org.example.project.data.repository.MatrimonyRepositoryImpl
import org.example.project.ui.viewmodels.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import java.util.prefs.Preferences

val appModule = module {
    single<HttpClient> {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                    explicitNulls = true
                })
            }
            install(DefaultRequest) {
                url("https://finder-api.chavaramatrimony.com/")
                contentType(ContentType.Application.Json)
                header("productcode", "fdae621a-9655-4c5f-b9ba-a4c51fabd5ae")
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val prefs = Preferences.userRoot().node("org.example.project.MatrimonyFinder")
                        val jsonStr = prefs.get("auth_user", null)
                        val tokenInfo = if (jsonStr != null) {
                            try {
                                val json = Json { ignoreUnknownKeys = true }
                                val response = json.decodeFromString<LoginResponse>(jsonStr)
                                Pair(response.token, response.refreshToken)
                            } catch (e: Exception) { null }
                        } else null

                        val token = tokenInfo?.first
                        val refreshToken = tokenInfo?.second ?: ""
                        
                        token?.let { BearerTokens(it, refreshToken) }
                    }
                    
                    refreshTokens {
                        val prefs = Preferences.userRoot().node("org.example.project.MatrimonyFinder")
                        val jsonStr = prefs.get("auth_user", null)
                        
                        var oldToken: String? = null
                        var oldRefreshToken: String? = null

                        if (jsonStr != null) {
                            try {
                                val json = Json { ignoreUnknownKeys = true }
                                val response = json.decodeFromString<LoginResponse>(jsonStr)
                                oldToken = response.token
                                oldRefreshToken = response.refreshToken
                            } catch (e: Exception) { }
                        }

                        if (oldRefreshToken.isNullOrEmpty() || oldToken.isNullOrEmpty()) {
                            return@refreshTokens null
                        }

                        try {
                            val refreshResponse = client.post("Auth/refresh-token") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody("""{"token":"$oldToken","refreshToken":"$oldRefreshToken"}""")
                            }

                            if (refreshResponse.status == HttpStatusCode.OK) {
                                val responseBody = refreshResponse.body<LoginResponse>()
                                val newToken = responseBody.token ?: return@refreshTokens null
                                val newRefreshToken = responseBody.refreshToken ?: oldRefreshToken

                                // Update preferences with new tokens
                                if (jsonStr != null) {
                                    try {
                                        val json = Json { ignoreUnknownKeys = true }
                                        val oldResponse = json.decodeFromString<LoginResponse>(jsonStr)
                                        val updatedResponse = oldResponse.copy(token = newToken, refreshToken = newRefreshToken)
                                        prefs.put("auth_user", json.encodeToString(LoginResponse.serializer(), updatedResponse))
                                    } catch (e: Exception) { }
                                }

                                BearerTokens(newToken, newRefreshToken)
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    sendWithoutRequest { true }
                }
            }
        }
    }

    singleOf(::MatrimonyRepositoryImpl) { bind<MatrimonyRepository>() }
    
    viewModelOf(::DashboardViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MyMatchesViewModel)
    viewModelOf(::WhoViewedMeViewModel)
    viewModelOf(::ProfileViewedByMeViewModel)
    viewModelOf(::MutualMatchesViewModel)
    viewModelOf(::NewlyJoinedViewModel)
    viewModelOf(::ContactViewsViewModel)
    viewModelOf(::ContactsViewedByMeViewModel)
}

