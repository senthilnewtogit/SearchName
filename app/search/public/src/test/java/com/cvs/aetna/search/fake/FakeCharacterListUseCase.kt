package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase

class FakeCharacterListUseCase :
    CharacterListUseCase,
    FakeFunctionHelper<FakeCharacterListUseCase.Function> {

    sealed class Function {
        data class GetCharacterList(val name: String) : Function()
    }
    var characterList: CharacterList = CharacterList(emptyList())
    override suspend fun getCharacterList(name: String): CharacterList {
        recordCalledFunction(Function.GetCharacterList(name))
        return characterList
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
