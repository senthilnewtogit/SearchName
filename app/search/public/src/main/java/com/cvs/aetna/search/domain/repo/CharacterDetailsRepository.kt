package com.cvs.aetna.search.domain.repo

import com.cvs.aetna.search.domain.model.CharacterDetails

interface CharacterDetailsRepository {
    suspend fun getCharacterDetails(id: String): CharacterDetails
}
