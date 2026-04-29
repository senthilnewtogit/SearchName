package com.cvs.aetna.search.domain.repo

import com.cvs.aetna.search.domain.model.CharacterList

interface CharacterListRepository {
    suspend fun getCharacterList(name: String): CharacterList
}
