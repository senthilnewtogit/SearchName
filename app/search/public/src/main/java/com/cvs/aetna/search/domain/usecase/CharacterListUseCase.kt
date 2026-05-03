package com.cvs.aetna.search.domain.usecase

import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch

interface CharacterListUseCase {
    suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList

    suspend fun getMoreCharacterList(url: String): CharacterList
}
