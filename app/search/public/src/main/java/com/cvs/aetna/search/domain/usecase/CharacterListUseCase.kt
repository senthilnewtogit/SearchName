package com.cvs.aetna.search.domain.usecase

import com.cvs.aetna.search.domain.model.CharacterList

interface CharacterListUseCase {
    suspend fun getCharacterList(name: String): CharacterList
}
