package com.cvs.aetna.search.domain.usecase

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.ShareData

interface ShareCharacterUseCase {
    suspend fun shareCharacterImage(
        character: CharacterDetails,
    ): ShareData
}
