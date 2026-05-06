package org.example.project.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.data.repository.MatrimonyRepository
import org.example.project.data.repository.MatrimonyRepositoryImpl
import org.example.project.ui.viewmodels.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

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
        }
    }

    singleOf(::MatrimonyRepositoryImpl) { bind<MatrimonyRepository>() }
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::MyMatchesViewModel)
    viewModelOf(::WhoViewedMeViewModel)
    viewModelOf(::ProfileViewedByMeViewModel)
    viewModelOf(::MutualMatchesViewModel)
    viewModelOf(::NewlyJoinedViewModel)
}
