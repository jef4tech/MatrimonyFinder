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
    suspend fun getContactViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse>
    suspend fun getContactsViewedByMe(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse>
}

class MatrimonyRepositoryImpl(
    private val client: HttpClient
) : MatrimonyRepository {
    companion object {
        private const val CLIENT_NAME_PROFILE_VIEWS = "Profile Views"
        private const val CLIENT_NAME_PROFILES_VIEWED_BY_ME = "Profiles Viewed By Me"
        private const val CLIENT_NAME_CONTACT_VIEWS = "Contact Views"
        private const val CLIENT_NAME_CONTACTS_VIEWED_BY_ME = "Contacts Viewed By Me"
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
        return safeApiCall<LoginResponse> {
            client.post("Auth/login-v2") {
                header("accept", "application/json, text/plain, */*")
                header("accept-language", "en,en;q=0.9,en;q=0.8")
                header("priority", "u=1, i")
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
        }.fold(
            onSuccess = { if (it.success == true) Result.success(it) else Result.failure(Exception(it.message ?: "Login failed")) },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun getMyMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> =
        safeApiCall {
            client.post("candidate/search/V4/myMatch/0/$candidateId/1") { setBody(request) }
        }

    override suspend fun getProfileViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> =
        safeApiCall {
            val code = resolveClientCode(token, CLIENT_NAME_PROFILE_VIEWS)
            client.put("CandidateView/v1/list/client/$code") { setBody(request) }
        }

    override suspend fun getProfileViewedByMe(clientId: String, token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> =
        safeApiCall {
            val code = resolveClientCode(token, CLIENT_NAME_PROFILES_VIEWED_BY_ME)
            client.put("CandidateView/v1/list/client/$code") { setBody(request) }
        }

    override suspend fun getContactViews(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> =
        safeApiCall {
            val code = resolveClientCode(token, CLIENT_NAME_CONTACT_VIEWS)
            client.put("CandidateView/v1/list/client/$code") { setBody(request) }
        }

    override suspend fun getContactsViewedByMe(token: String, request: ProfileViewsRequest): Result<ProfileViewsResponse> =
        safeApiCall {
            val code = resolveClientCode(token, CLIENT_NAME_CONTACTS_VIEWED_BY_ME)
            client.put("CandidateView/v1/list/client/$code") { setBody(request) }
        }

    override suspend fun getMutualMatches(candidateId: String, token: String, request: MyMatchesRequest): Result<MyMatchesResponse> =
        safeApiCall {
            client.post("candidate/search/V4/mutual/0/$candidateId/1") { setBody(request) }
        }

    override suspend fun getNewlyJoinedProfiles(candidateId: String, token: String, request: NewlyJoinedRequest): Result<MyMatchesResponse> =
        safeApiCall {
            client.post("candidate/search/v1/preference/V4/0/$candidateId/1") { setBody(request) }
        }

    override suspend fun getCandidateViewCounts(token: String): Result<List<CandidateViewCount>> =
        safeApiCall {
            client.get("CandidateView/client/count") {
                header("accept", "application/octet-stream")
            }
        }

    private suspend inline fun <reified T> safeApiCall(apiCall: suspend () -> HttpResponse): Result<T> {
        return try {
            val response = apiCall()
            if (response.status.isSuccess()) {
                Result.success(response.body<T>())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("HTTP ${response.status.value} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
