package org.example.project.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class CandidateProfile(
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
    val photo: Photos? = null,
    val messageStatus: MessageStatus? = null,
    val genderCode: String? = null,
    val complexion: String? = null,
    val maritalStatus: String? = null,
    val branch: String? = null
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

@Serializable
data class Pagination(
    val currentPage: Int,
    val recordCount: Int
)

@Serializable
data class SortOption(
    val typeCode: String,
    val name: String,
    val order: Int,
    val isSelected: Boolean
)

@Serializable
data class ProfileOptions(
    val isAlreadySeen: Boolean,
    val isAlreadyContacted: Boolean,
    val isInterestSent: Boolean,
    val isShortListed: Boolean,
    val isWithPhoto: Boolean,
    val isOnline: Boolean,
    val isPremium: Boolean
)
