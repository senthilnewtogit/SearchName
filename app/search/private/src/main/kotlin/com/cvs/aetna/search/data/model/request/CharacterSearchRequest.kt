package com.cvs.aetna.search.data.model.request

import kotlin.let

data class CharacterSearchRequest(
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
)

private const val NAME = "name"

private const val STATUS = "status"

private const val SPECIES = "species"

private const val TYPE = "type"

fun CharacterSearchRequest.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()

    name?.takeIf { it.isNotBlank() }?.let { map[NAME] = it }
    status?.let { map[STATUS] = it.lowercase() }
    species?.let { map[SPECIES] = it.lowercase() }
    type?.takeIf { it.isNotBlank() }?.let { map[TYPE] = it }
    return map
}
