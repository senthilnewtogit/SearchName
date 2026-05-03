package com.cvs.aetna.search.domain.repo

import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch

interface CharacterListRepository {
    suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList

    suspend fun getMoreCharacterList(url: String): CharacterList
}
