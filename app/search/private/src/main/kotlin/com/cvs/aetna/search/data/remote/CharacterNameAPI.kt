package com.cvs.aetna.search.data.remote

import com.cvs.aetna.search.data.model.Character
import com.cvs.aetna.search.data.model.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterNameAPI {

    companion object {
        const val LIST_OF_CHARACTER = "/api/character/"
        const val CHARACTER_DETAILS = "/api/character/{id}"
    }

    @GET(LIST_OF_CHARACTER)
    suspend fun getListOfCharacter(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null,
    ): Response<CharacterResponse>

    @GET(CHARACTER_DETAILS)
    suspend fun getCharacterDetails(
        @Path("id") id: String? = null,
    ): Response<Character>
}
