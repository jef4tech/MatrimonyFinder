package org.example.project

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

object ApiClient {
    private const val BASE_URL = "https://finder-api.chavaramatrimony.com"
    
    private val client = HttpClient(CIO) {
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

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post("$BASE_URL/Auth/login-v2") {
                contentType(ContentType.Application.Json)
                header("accept", "application/json, text/plain, */*")
                header("accept-language", "en,en;q=0.9,en;q=0.8")
                header("priority", "u=1, i")
                header("productcode", "fdae621a-9655-4c5f-b9ba-a4c51fabd5ae")
                header("sec-ch-ua", "\"Not:A-Brand\";v=\"99\", \"Brave\";v=\"145\", \"Chromium\";v=\"145\"")
                header("sec-ch-ua-mobile", "?0")
                header("sec-ch-ua-platform", "\"Windows\"")
                header("sec-fetch-dest", "empty")
                header("sec-fetch-mode", "cors")
                header("sec-fetch-site", "same-site")
                header("sec-gpc", "1")
                header("referrer", "https://www.chavaramatrimony.com/")
                setBody(request)
            }
            
            if (response.status.isSuccess()) {
                val result = response.body<LoginResponse>()
                if (result.success == true) {
                    Result.success(result)
                } else {
                    Result.failure(Exception(result.message ?: "Login failed"))
                }
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> {
        return try {
            val response = client.post("$BASE_URL/candidate/search/V4/myMatch/0/$candidateId/1") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getProfileViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> {
        return try {
            val response = client.put("$BASE_URL/CandidateView/v1/list/client/c810eefd-035c-4362-b2c8-0c1415563bd7") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfileViewedByMe(clientId: String, token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> {
        return try {
            val clientId = "13267262-7f38-44a8-bd96-0f92259dae39"
            val response = client.put("$BASE_URL/CandidateView/v1/list/client/$clientId") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMutualMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> {
        return try {
            val response = client.post("$BASE_URL/candidate/search/V4/mutual/0/$candidateId/1") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNewlyJoinedProfiles(candidateId: String, token: String, request: NewlyJoinedRequest): Result<MyMatchesResponse> {
        return try {
            val response = client.post("$BASE_URL/candidate/search/v1/preference/V4/0/$candidateId/1") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
