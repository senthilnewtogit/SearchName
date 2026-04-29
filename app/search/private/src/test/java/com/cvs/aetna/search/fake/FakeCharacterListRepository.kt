package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.repo.CharacterListRepository

class FakeCharacterListRepository :
    CharacterListRepository,
    FakeFunctionHelper<FakeCharacterListRepository.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
    private var shouldReturnError = false
    private var errorMessage: String? = null

    sealed class Function {
        data class GetCharacterList(val name: String) : Function()
    }
    fun setShouldReturnError(shouldError: Boolean, message: String? = null) {
        shouldReturnError = shouldError
        errorMessage = message
    }

    fun reset() {
        shouldReturnError = false
        errorMessage = null
    }

    override suspend fun getCharacterList(name: String): CharacterList {
        recordCalledFunction(Function.GetCharacterList(name))

        if (shouldReturnError) {
            return CharacterList(
                characters = emptyList(),
                hasMorePage = false,
                nextPageUrl = null,
                errorMsg = errorMessage ?: "Fake repository error",
                hasError = true,
            )
        }

        val allCharacters = getMockCharacters()
        val filteredCharacters = if (name.isBlank()) {
            allCharacters
        } else {
            allCharacters.filter { it.name?.contains(name, ignoreCase = true) == true }
        }

        return CharacterList(
            characters = filteredCharacters,
            hasMorePage = filteredCharacters.size > 3, // Simulate pagination
            nextPageUrl = if (filteredCharacters.size > 3) "https://rickandmortyapi.com/api/character/?page=2&name=$name" else null,
            errorMsg = null,
            hasError = false,
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
