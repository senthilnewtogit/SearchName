package com.cvs.aetna.search.domain.usecase

import com.cvs.aetna.search.domain.model.CharacterDetails

interface CharacterDetailsUseCase {
    suspend fun getCharacterDetails(id: String): CharacterDetails
}
