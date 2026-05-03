package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.response.Character
import com.cvs.aetna.search.data.remote.CharacterNameAPI
import com.cvs.aetna.search.data.transformer.CharacterDetailsResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.repo.CharacterDetailsRepository
import com.cvs.aetna.search.logger.TelemetryService
import com.google.gson.Gson
import javax.inject.Inject

class DefaultCharacterDetailsRepository @Inject constructor(
    private val characterNameAPI: CharacterNameAPI,
    private val detailsResponseToDomainTransform: CharacterDetailsResponseToDomainTransform,
    private val telemetryService: TelemetryService,
) : CharacterDetailsRepository {

    override suspend fun getCharacterDetails(id: String): CharacterDetails = try {
        val characterDetails = characterNameAPI.getCharacterDetails(id = id)
        if (characterDetails.isSuccessful) {
            detailsResponseToDomainTransform.transform(characterDetails.body())
        } else {
            val errorResponse =
                Gson().fromJson(characterDetails.errorBody()?.string(), Character::class.java)
            detailsResponseToDomainTransform.transform(errorResponse)
        }
    } catch (e: Exception) {
        telemetryService.logEvent(
            eventName = "GetCharacterDetailsError",
            properties = mapOf("error" to (e.message ?: "Unknown error")),
        )
        CharacterDetails(
            hasError = true,
            errorMsg = e.message ?: "Unknown error",
        )
    }
}
