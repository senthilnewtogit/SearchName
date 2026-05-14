package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.repo.CharacterDetailsRepository
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
import javax.inject.Inject

class DefaultCharacterDetailsUseCase @Inject constructor(private val characterDetailsRepository: CharacterDetailsRepository) : CharacterDetailsUseCase {
    override suspend fun getCharacterDetails(id: String): CharacterDetails = characterDetailsRepository.getCharacterDetails(id)
}
