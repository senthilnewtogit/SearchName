package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase

class FakeCharacterListUseCase :
    CharacterListUseCase,
    FakeFunctionHelper<FakeCharacterListUseCase.Function> {

    sealed class Function {
        data class GetCharacterList(val characterSearch: CharacterSearch) : Function()

        data class GetMoreCharacterList(val url: String) : Function()
    }
    var characterList: CharacterList = CharacterList(emptyList())
    var moreCharacterList: CharacterList = CharacterList(emptyList())

    override suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList {
        recordCalledFunction(Function.GetCharacterList(characterSearch = characterSearch))
        return characterList
    }

    override suspend fun getMoreCharacterList(url: String): CharacterList {
        recordCalledFunction(Function.GetMoreCharacterList(url = url))
        return moreCharacterList
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
