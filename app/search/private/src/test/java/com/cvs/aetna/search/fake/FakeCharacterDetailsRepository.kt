package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.repo.CharacterDetailsRepository

class FakeCharacterDetailsRepository :
    CharacterDetailsRepository,
    FakeFunctionHelper<FakeCharacterDetailsRepository.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()

    sealed class Function {
        data class GetCharacterDetails(val id: String) : Function()
    }
    private var shouldReturnError = false
    private var errorMessage: String? = null

    fun setShouldReturnError(shouldError: Boolean, message: String? = null) {
        shouldReturnError = shouldError
        errorMessage = message
    }

    override suspend fun getCharacterDetails(id: String): CharacterDetails {
        recordCalledFunction(Function.GetCharacterDetails(id))
        return if (shouldReturnError) {
            CharacterDetails(
                hasError = true,
                errorMsg = errorMessage ?: "Character not found",
            )
        } else {
            getMockCharacterById(id)
        }
    }

    private fun getMockCharacterById(id: String): CharacterDetails = when (id.toIntOrNull()) {
        1 -> CharacterDetails(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",
            createdAt = "2017-11-04T18:48:46.250Z",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            hasError = false,
            errorMsg = null,
        )
        2 -> CharacterDetails(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",
            createdAt = "2017-11-04T20:32:59.209Z",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            hasError = false,
            errorMsg = null,
        )
        3 -> CharacterDetails(
            id = 3,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",
            createdAt = "2017-11-04T20:33:30.896Z",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
            hasError = false,
            errorMsg = null,
        )
        4 -> CharacterDetails(
            id = 4,
            name = "Beth Smith",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",
            createdAt = "2017-11-04T20:33:30.896Z",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
            hasError = false,
            errorMsg = null,
        )
        5 -> CharacterDetails(
            id = 5,
            name = "Jerry Smith",
            status = "Alive",
            species = "Human",
            type = "",
            origin = "Earth (C-137)",
            createdAt = "2017-11-04T20:33:30.896Z",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/5.jpeg",
            hasError = false,
            errorMsg = null,
        )
        else -> CharacterDetails(
            hasError = true,
            errorMsg = "Character with id $id not found",
        )
    }

    fun reset() {
        shouldReturnError = false
        errorMessage = null
    }
}
