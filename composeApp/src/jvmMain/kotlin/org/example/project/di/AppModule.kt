package org.example.project.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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
                        val token = if (jsonStr != null) {
                            try {
                                val json = Json { ignoreUnknownKeys = true }
                                val response = json.decodeFromString<LoginResponse>(jsonStr)
                                response.token
                            } catch (e: Exception) { null }
                        } else null

                        token?.let { BearerTokens(it, "") }
                    }
                    sendWithoutRequest { true }
                }
            }
        }
    }

    singleOf(::MatrimonyRepositoryImpl) { bind<MatrimonyRepository>() }
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::MyMatchesViewModel)
    viewModelOf(::WhoViewedMeViewModel)
    viewModelOf(::ProfileViewedByMeViewModel)
    viewModelOf(::MutualMatchesViewModel)
    viewModelOf(::NewlyJoinedViewModel)
    viewModelOf(::ContactViewsViewModel)
    viewModelOf(::ContactsViewedByMeViewModel)
}

