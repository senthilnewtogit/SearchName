package com.cvs.aetna.search.domain.model

data class CharacterList(
    val characters: List<CharacterDetails>? = null,
    val totalCount: Int = 0,
    val hasMorePage: Boolean = false,
    val nextPageUrl: String? = null,
    val errorMsg: CharacterError? = null,
) {
    val hasError: Boolean get() = errorMsg != null
}

data class CharacterDetails(
    val id: Int? = null,
    val name: String? = null,
    val status: String? = null,
    val origin: String? = null,
    val species: String? = null,
    val type: String? = null,
    val createdAt: String? = null,
    val imageUrl: String? = null,
    val hasError: Boolean = false,
    val errorMsg: String? = null,
)
data class ShareData(
    val fileUri: String,
    val shareText: String,
)
data class CharacterSearch(
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
)
sealed class CharacterError {

    data class NoInternet(val message: String? = null) : CharacterError()
    data class Timeout(val message: String? = null) : CharacterError()
    data class NetworkIO(val message: String? = null) : CharacterError()
    data class UnknownHost(val message: String? = null) : CharacterError()
    data class SSL(val message: String? = null) : CharacterError()
    data class Http(val code: Int, val message: String? = null) : CharacterError()
    data class RateLimit(val retryAfterSeconds: Long) : CharacterError()
    data class Parsing(val message: String? = null) : CharacterError()
    data class Unknown(val message: String? = null) : CharacterError()
    data class NoResultFound(val message: String? = null) : CharacterError()
}
