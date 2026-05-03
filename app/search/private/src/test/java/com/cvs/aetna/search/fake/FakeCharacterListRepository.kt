package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
import com.cvs.aetna.search.domain.repo.CharacterListRepository

class FakeCharacterListRepository :
    CharacterListRepository,
    FakeFunctionHelper<FakeCharacterListRepository.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
    private var shouldReturnError = false
    private var errorMessage: CharacterError? = null

    sealed class Function {
        data class GetCharacterList(val characterSearch: CharacterSearch) : Function()

        data class GetMoreCharacterList(val url: String) : Function()
    }
    fun setShouldReturnError(shouldError: Boolean, characterError: CharacterError? = null) {
        shouldReturnError = shouldError
        errorMessage = characterError
    }

    fun reset() {
        shouldReturnError = false
        errorMessage = null
    }

    override suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList {
        recordCalledFunction(Function.GetCharacterList(characterSearch))

        if (shouldReturnError) {
            return CharacterList(
                characters = emptyList(),
                hasMorePage = false,
                nextPageUrl = null,
                errorMsg = errorMessage ?: CharacterError.Unknown("Fake repository error"),
            )
        }

        val allCharacters = getMockCharacters()
        val filteredCharacters = if ((characterSearch.name ?: "").isBlank()) {
            allCharacters
        } else {
            allCharacters.filter { it.name?.contains(other = characterSearch.name ?: "", ignoreCase = true) == true }
        }

        return CharacterList(
            characters = filteredCharacters,
            hasMorePage = filteredCharacters.size > 3,
            totalCount = filteredCharacters.size,
            nextPageUrl = if (filteredCharacters.size > 3) "https://rickandmortyapi.com/api/character/?page=2&name=$characterSearch" else null,
            errorMsg = null,
        )
    }

    override suspend fun getMoreCharacterList(url: String): CharacterList {
        recordCalledFunction(Function.GetMoreCharacterList(url = url))
        if (shouldReturnError) {
            return CharacterList(
                characters = emptyList(),
                hasMorePage = false,
                nextPageUrl = null,
                errorMsg = errorMessage ?: CharacterError.Unknown("Fake repository error"),
            )
        }
        val allCharacters = getMockCharacters()
        val filteredCharacters = if (url.isBlank()) {
            allCharacters
        } else {
            allCharacters.filter { it.name?.contains(other = url, ignoreCase = true) == true }
        }

        return CharacterList(
            characters = filteredCharacters,
            hasMorePage = filteredCharacters.size > 3,
            totalCount = filteredCharacters.size,
            nextPageUrl = if (filteredCharacters.size > 3) "https://rickandmortyapi.com/api/character/?page=2&name=$url" else null,
            errorMsg = null,
        )
    }

    private fun getMockCharacters(): List<CharacterDetails> = listOf(
        CharacterDetails(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",

            origin = "Earth (C-137)",

            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            createdAt = "2017-11-04T18:48:46.250Z",
        ),
        CharacterDetails(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            type = "",

            origin = "Earth (C-137)",

            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            createdAt = "2017-11-04T20:32:59.209Z",
        ),
        CharacterDetails(
            id = 3,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            type = "",

            origin = "Earth (C-137)",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
            createdAt = "2017-11-04T20:33:30.896Z",
        ),
        CharacterDetails(
            id = 4,
            name = "Beth Smith",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",

            imageUrl = "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
            createdAt = "2017-11-04T20:33:30.896Z",
        ),
        CharacterDetails(
            id = 5,
            name = "Jerry Smith",
            status = "Alive",
            species = "Human",
            type = "",

            origin = "Earth (C-137)",

            imageUrl = "https://rickandmortyapi.com/api/character/avatar/5.jpeg",
            createdAt = "2017-11-04T20:33:30.896Z",
        ),
    )
}
