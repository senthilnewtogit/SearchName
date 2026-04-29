package com.cvs.aetna.search.domain.model

data class CharacterList(
    val characters: List<CharacterDetails>? = null,
    val hasMorePage: Boolean = false,
    val nextPageUrl: String? = null,
    val hasError: Boolean = false,
    val errorMsg: String? = null,
)
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
