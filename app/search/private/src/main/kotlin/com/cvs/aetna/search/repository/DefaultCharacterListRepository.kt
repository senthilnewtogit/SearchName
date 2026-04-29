package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.CharacterResponse
import com.cvs.aetna.search.data.remote.CharacterNameAPI
import com.cvs.aetna.search.data.transformer.CharacterResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.repo.CharacterListRepository
import com.cvs.aetna.search.logger.TelemetryService
import com.google.gson.Gson
import javax.inject.Inject

class DefaultCharacterListRepository @Inject constructor(
    private val characterNameAPI: CharacterNameAPI,
    private val characterResponseToDomainTransform: CharacterResponseToDomainTransform,
    private val telemetryService: TelemetryService,
) : CharacterListRepository {
    override suspend fun getCharacterList(name: String): CharacterList = try {
        val characterResponse = characterNameAPI.getListOfCharacter(name = name)
        if (characterResponse.isSuccessful) {
            characterResponseToDomainTransform.transform(characterResponse.body())
        } else {
            val errorJsonString = characterResponse.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorJsonString, CharacterResponse::class.java)
            characterResponseToDomainTransform.transform(errorResponse)
        }
    } catch (e: Exception) {
        telemetryService.logEvent(
            eventName = "GetCharacterListError",
            properties = mapOf("error" to (e.message ?: "Unknown error")),
        )
        CharacterList(
            hasError = true,
            errorMsg = e.message ?: "Unknown error",
        )
    }
}
