package org.example.project.data.remote.models

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
    val isOnline: Boolean? = null,
    val religion: String? = null,
    val educationDetails: String? = null,
    val profession: Profession? = null,
    val workingState: String? = null,
    val workingCountry: String? = null,
    val photos: Photos? = null,
    val messageStatus: MessageStatus? = null
)

@Serializable
data class Profession(
    val name: String? = null,
    val organization: String? = null,
    val details: String? = null,
    val workingPlace: String? = null,
    val professionName: String? = null
)

@Serializable
data class Photos(
    val candidatePhotos: List<CandidatePhoto>? = null
)

@Serializable
data class CandidatePhoto(
    val displayPhotoUrl: String? = null
)

@Serializable
data class MessageStatus(
    val colour: String? = null,
    val code: String? = null,
    val message: String? = null,
    val isExpired: Boolean? = null,
    val warning: String? = null,
    val cometId: String? = null
)
