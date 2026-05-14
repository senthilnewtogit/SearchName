package com.cvs.aetna.search.data.mapper

import com.cvs.aetna.search.data.model.request.CharacterSearchRequest
import com.cvs.aetna.search.domain.model.CharacterSearch

fun CharacterSearch.toRequest(): CharacterSearchRequest = CharacterSearchRequest(
    name = name,
    status = status,
    species = species,
    type = type,
)
