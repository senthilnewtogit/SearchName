package com.cvs.aetna.search.data.model.response

import com.cvs.aetna.search.domain.model.CharacterDetails
import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val info: PaginationInfo? = null,
    val results: List<Character>? = null,
)

@Serializable
data class PaginationInfo(
    val count: Int? = null,
    val pages: Int? = null,
    val next: String? = null,
    val prev: String? = null,
)

@Serializable
data class Character(
    val id: Int? = null,
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
    val gender: String? = null,
    val origin: Location? = null,
    val location: Location? = null,
    val image: String? = null,
    val episode: List<String>? = null,
    val url: String? = null,
    val created: String? = null,
    val error: String? = null,
)

@Serializable
data class Location(
    val name: String? = null,
    val url: String? = null,
)

fun Character.toDomain() = CharacterDetails(
    id = id,
    name = name?.trim(),
    status = status?.trim(),
    origin = origin?.name?.trim(),
    species = species?.trim(),
    type = type?.trim(),
    createdAt = created?.trim(),
    imageUrl = image?.trim(),
)
