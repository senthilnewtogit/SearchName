package com.cvs.aetna.search.data.transformer

import com.cvs.aetna.search.data.model.response.Character
import com.cvs.aetna.search.domain.model.CharacterDetails
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

interface CharacterDetailsResponseToDomainTransform {
    fun transform(characterDetailsResponse: Character?): CharacterDetails
}

private const val MMM_DD_YYYY = "MMM dd, yyyy"

class DefaultCharacterToDomainTransform @Inject constructor() : CharacterDetailsResponseToDomainTransform {
    override fun transform(characterDetailsResponse: Character?): CharacterDetails = CharacterDetails(
        id = characterDetailsResponse?.id,
        name = characterDetailsResponse?.name?.trim(),
        status = characterDetailsResponse?.status?.trim(),
        origin = characterDetailsResponse?.origin?.name?.trim(),
        species = characterDetailsResponse?.species?.trim(),
        type = characterDetailsResponse?.type?.trim(),
        createdAt = getCreatedDate(characterDetailsResponse?.created?.trim()),
        imageUrl = characterDetailsResponse?.image?.trim(),
        hasError = characterDetailsResponse?.error != null,
        errorMsg = characterDetailsResponse?.error,
    )

    private fun getCreatedDate(
        createdAt: String?,
        locale: Locale = Locale.getDefault(),
    ): String? = createdAt?.let { createdAt ->
        try {
            val zonedDateTime = ZonedDateTime.parse(createdAt)
            val formatter = DateTimeFormatter.ofPattern(MMM_DD_YYYY, locale)
            zonedDateTime.format(formatter)
        } catch (_: Exception) {
            createdAt
        }
    }
}
