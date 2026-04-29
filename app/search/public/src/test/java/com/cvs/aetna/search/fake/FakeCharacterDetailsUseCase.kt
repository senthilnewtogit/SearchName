package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase

class FakeCharacterDetailsUseCase :
    CharacterDetailsUseCase,
    FakeFunctionHelper<FakeCharacterDetailsUseCase.Function> {

    sealed class Function {
        data class GetCharacterDetails(val id: String) : Function()
    }

    var characterDetails: CharacterDetails = CharacterDetails()

    override suspend fun getCharacterDetails(id: String): CharacterDetails {
        recordCalledFunction(Function.GetCharacterDetails(id))
        return characterDetails
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
