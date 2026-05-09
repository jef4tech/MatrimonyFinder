package org.example.project.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private const val CLIENT_NAME_PROFILE_VIEWS = "Profile Views"
        private const val CLIENT_NAME_PROFILES_VIEWED_BY_ME = "Profiles Viewed By Me"
    }

    private var cachedClientCounts: List<CandidateViewCount>? = null
    private val countsMutex = Mutex()

    private suspend fun ensureClientCounts(token: String): List<CandidateViewCount> {
        cachedClientCounts?.let { return it }
        return countsMutex.withLock {
            cachedClientCounts ?: run {
                val list = getCandidateViewCounts(token).getOrThrow()
                cachedClientCounts = list
                list
            }
        }
    }

    private suspend fun resolveClientCode(token: String, name: String): String {
        val list = ensureClientCounts(token)
        return list.firstOrNull { it.name == name }?.code
            ?: throw IllegalStateException("No client code found for '$name'")
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
            val code = resolveClientCode(token, CLIENT_NAME_PROFILE_VIEWS)
            val response = client.put("$BASE_URL/CandidateView/v1/list/client/$code") {
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
            val code = resolveClientCode(token, CLIENT_NAME_PROFILES_VIEWED_BY_ME)
            val response = client.put("$BASE_URL/CandidateView/v1/list/client/$code") {
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
