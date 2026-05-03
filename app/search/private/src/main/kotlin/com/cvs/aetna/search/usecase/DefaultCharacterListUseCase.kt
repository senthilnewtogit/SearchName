package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
import com.cvs.aetna.search.domain.repo.CharacterListRepository
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import javax.inject.Inject

class DefaultCharacterListUseCase @Inject constructor(private val characterListRepository: CharacterListRepository) : CharacterListUseCase {
    override suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList = characterListRepository.getCharacterList(
        characterSearch = characterSearch,
    )

    override suspend fun getMoreCharacterList(url: String): CharacterList = characterListRepository.getMoreCharacterList(url = url)
}
