package com.cvs.aetna.search.data.transformer

import com.cvs.aetna.search.data.model.CharacterResponse
import com.cvs.aetna.search.data.model.toDomain
import com.cvs.aetna.search.domain.model.CharacterList
import javax.inject.Inject

interface CharacterResponseToDomainTransform {
    fun transform(characterResponse: CharacterResponse?): CharacterList
}

class DefaultCharacterListToDomainTransform @Inject constructor() : CharacterResponseToDomainTransform {
    override fun transform(characterResponse: CharacterResponse?): CharacterList = CharacterList(
        characters = characterResponse?.results?.map {
            it.toDomain()
        } ?: emptyList(),
        hasMorePage = characterResponse?.info?.next != null,
        nextPageUrl = characterResponse?.info?.next,
        errorMsg = characterResponse?.error,
        hasError = characterResponse?.error != null,
    )
}
