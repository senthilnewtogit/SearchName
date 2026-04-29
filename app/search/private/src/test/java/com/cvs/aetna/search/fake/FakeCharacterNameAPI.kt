package com.cvs.aetna.search.fake

import com.cvs.aetna.search.data.model.Character
import com.cvs.aetna.search.data.model.CharacterResponse
import com.cvs.aetna.search.data.model.Location
import com.cvs.aetna.search.data.model.PaginationInfo
import com.cvs.aetna.search.data.remote.CharacterNameAPI
import okhttp3.ResponseBody
import retrofit2.Response

class FakeCharacterNameAPI :
    CharacterNameAPI,
    FakeFunctionHelper<FakeCharacterNameAPI.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
    sealed class Function {
        data class GetListOfCharacter(val name: String?, val page: Int?) : Function()
        data class GetCharacterDetails(val id: String?) : Function()
    }

    private var shouldReturnError = false
    private var errorMessage: String? = null

    fun setShouldReturnError(shouldError: Boolean, message: String? = null) {
        shouldReturnError = shouldError
        errorMessage = message
    }

    override suspend fun getListOfCharacter(
        name: String?,
        page: Int?,
    ): Response<CharacterResponse> {
        recordCalledFunction(Function.GetListOfCharacter(name, page))
        return if (shouldReturnError) {
            Response.error(404, ResponseBody.create(null, errorMessage ?: "Not Found"))
        } else {
            val mockCharacters = getMockCharacterList(name)
            val paginationInfo = PaginationInfo(
                count = mockCharacters.size,
                pages = 1,
                next = if (page == null || page < 5) "https://rickandmortyapi.com/api/character/?page=${(page ?: 1) + 1}&name=$name" else null,
                prev = if (page != null && page > 1) "https://rickandmortyapi.com/api/character/?page=${page - 1}&name=$name" else null,
            )
            val characterResponse = CharacterResponse(
                info = paginationInfo,
                results = mockCharacters,
            )
            Response.success(characterResponse)
        }
    }

    override suspend fun getCharacterDetails(
        id: String?,
    ): Response<Character> {
        recordCalledFunction(Function.GetCharacterDetails(id))
        return if (shouldReturnError) {
            Response.error(404, ResponseBody.create(null, errorMessage ?: "Not Found"))
        } else {
            val character = getMockCharacterById(id)
            if (character != null) {
                Response.success(character)
            } else {
                Response.error(404, ResponseBody.create(null, "Character not found"))
            }
        }
    }

    private fun getMockCharacterList(name: String?): List<Character> {
        val allCharacters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                location = Location(
                    name = "Citadel of Ricks",
                    url = "https://rickandmortyapi.com/api/location/3",
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                ),
                url = "https://rickandmortyapi.com/api/character/1",
                created = "2017-11-04T18:48:46.250Z",
            ),
            Character(
                id = 2,
                name = "Morty Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                location = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                ),
                url = "https://rickandmortyapi.com/api/character/2",
                created = "2017-11-04T20:32:59.209Z",
            ),
            Character(
                id = 3,
                name = "Summer Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Female",
                origin = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                location = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/6",
                    "https://rickandmortyapi.com/api/episode/7",
                ),
                url = "https://rickandmortyapi.com/api/character/3",
                created = "2017-11-04T20:33:30.896Z",
            ),
            Character(
                id = 4,
                name = "Beth Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Female",
                origin = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                location = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                ),
                url = "https://rickandmortyapi.com/api/character/4",
                created = "2017-11-04T20:33:30.896Z",
            ),
            Character(
                id = 5,
                name = "Jerry Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                location = Location(
                    name = "Earth (C-137)",
                    url = "https://rickandmortyapi.com/api/location/1",
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/5.jpeg",
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                ),
                url = "https://rickandmortyapi.com/api/character/5",
                created = "2017-11-04T20:33:30.896Z",
            ),
        )

        return if (name.isNullOrEmpty()) {
            allCharacters
        } else {
            allCharacters.filter { it.name?.contains(name, ignoreCase = true) == true }
        }
    }

    private fun getMockCharacterById(id: String?): Character? = getMockCharacterList(null).firstOrNull { it.id == id?.toIntOrNull() }
}
