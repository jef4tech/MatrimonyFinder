package org.example.project.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.example.project.data.remote.models.*

interface MatrimonyRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun getMyMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse>
    suspend fun getProfileViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse>
    suspend fun getProfileViewedByMe(clientId: String, token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse>
    suspend fun getMutualMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse>
    suspend fun getNewlyJoinedProfiles(candidateId: String, token: String, request: NewlyJoinedRequest): Result<MyMatchesResponse>
    suspend fun getCandidateViewCounts(token: String): Result<List<CandidateViewCount>>
}

class MatrimonyRepositoryImpl(
    private val client: HttpClient
) : MatrimonyRepository {
    companion object {
        private const val BASE_URL = "https://finder-api.chavaramatrimony.com"
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post("$BASE_URL/Auth/login-v2") {
                contentType(ContentType.Application.Json)
                header("accept", "application/json, text/plain, */*")
                header("accept-language", "en,en;q=0.9,en;q=0.8")
                header("priority", "u=1, i")
                header("productcode", "fdae621a-9655-4c5f-b9ba-a4c51fabd5ae")
//                header("sec-ch-ua", "\"Not:A-Brand\";v=\"99\", \"Brave\";v=\"145\", \"Chromium\";v=\"145\"")
//                header("sec-ch-ua-mobile", "?0")
//                header("sec-ch-ua-platform", "\"Windows\"")
//                header("sec-fetch-dest", "empty")
//                header("sec-fetch-mode", "cors")
//                header("sec-fetch-site", "same-site")
//                header("sec-gpc", "1")
//                header("referrer", "https://www.chavaramatrimony.com/")
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

    override suspend fun getMyMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> {
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

    override suspend fun getProfileViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> {
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

    override suspend fun getProfileViewedByMe(clientId: String, token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> {
        return try {
            val clientIdParam = "13267262-7f38-44a8-bd96-0f92259dae39"
            val response = client.put("$BASE_URL/CandidateView/v1/list/client/$clientIdParam") {
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

    override suspend fun getMutualMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> {
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

    override suspend fun getNewlyJoinedProfiles(candidateId: String, token: String, request: NewlyJoinedRequest): Result<MyMatchesResponse> {
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

    override suspend fun getCandidateViewCounts(token: String): Result<List<CandidateViewCount>> {
        return try {
            val response = client.get("$BASE_URL/CandidateView/client/count") {
                header("Authorization", "Bearer $token")
                header("accept", "application/octet-stream")
            }
            if (response.status.isSuccess()) {
                val text = response.bodyAsText()
                Result.success(countsJson.decodeFromString(text))
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val countsJson = Json { ignoreUnknownKeys = true; isLenient = true }
}
