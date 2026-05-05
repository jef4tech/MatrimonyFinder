package org.example.project

import kotlinx.serialization.Serializable

@Serializable
data class MyMatchesResponse(
    val items: List<MatchItem>? = null,
    val totalRecords: Int? = null
)

@Serializable
data class MatchItem(
    val candidateId: String? = null,
    val profileId: String? = null,
    val age: Int? = null,
    val heightInCentimeter: Int? = null,
    val isPremium: Boolean? = null,
    val educationDetails: String? = null,
    val profession: Profession? = null,
    val workingState: String? = null,
    val workingCountry: String? = null,
    val photos: Photos? = null
)

@Serializable
data class Profession(
    val details: String? = null,
    val organization: String? = null
)

@Serializable
data class Photos(
    val candidatePhotos: List<CandidatePhoto>? = null
)

@Serializable
data class CandidatePhoto(
    val displayPhotoUrl: String? = null
)
