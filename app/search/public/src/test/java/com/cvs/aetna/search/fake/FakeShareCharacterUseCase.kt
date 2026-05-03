package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.ShareData
import com.cvs.aetna.search.domain.usecase.ShareCharacterUseCase

class FakeShareCharacterUseCase :
    ShareCharacterUseCase,
    FakeFunctionHelper<FakeShareCharacterUseCase.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()

    sealed class Function {
        data class ShareCharacterImage(
            val character: CharacterDetails,
        ) : Function()
    }

    private var shareData: ShareData? = null
    override suspend fun shareCharacterImage(
        character: CharacterDetails,
    ): ShareData {
        recordCalledFunction(Function.ShareCharacterImage(character = character))
        return shareData ?: ShareData(fileUri = "", shareText = "")
    }
}
