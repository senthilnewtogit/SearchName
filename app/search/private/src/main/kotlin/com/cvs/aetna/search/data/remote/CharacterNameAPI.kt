package com.cvs.aetna.search.data.remote

import com.cvs.aetna.search.data.model.response.Character
import com.cvs.aetna.search.data.model.response.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface CharacterNameAPI {

    companion object {
        const val LIST_OF_CHARACTER = "/api/character/"
        const val CHARACTER_DETAILS = "/api/character/{id}"
    }

    @GET(LIST_OF_CHARACTER)
    suspend fun getListOfCharacter(
        @QueryMap query: Map<String, String>,
    ): Response<CharacterResponse>

    @GET
    suspend fun getMoreListOfCharacter(
        @Url url: String,
    ): Response<CharacterResponse>

    @GET(CHARACTER_DETAILS)
    suspend fun getCharacterDetails(
        @Path("id") id: String? = null,
    ): Response<Character>
}
