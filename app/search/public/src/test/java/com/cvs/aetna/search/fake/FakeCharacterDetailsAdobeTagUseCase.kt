package com.cvs.aetna.search.fake

import com.cvs.aetna.search.analytics.CharacterDetailsAdobeTagUseCase

class FakeCharacterDetailsAdobeTagUseCase :
    CharacterDetailsAdobeTagUseCase,
    FakeFunctionHelper<FakeCharacterDetailsAdobeTagUseCase.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
    sealed class Function {
        object OnDetailsScreenLoad : Function()

        object OnDetailsFetchAction : Function()
        object OnDetailsShareAction : Function()
        object OnDetailsShareNotFoundAction : Function()
        data class OnDetailsErrorEvent(val message: String) : Function()
    }
    override fun tagOnDetailsScreenLoad() {
        recordCalledFunction(Function.OnDetailsScreenLoad)
    }

    override fun tagOnDetailsFetchAction() {
        recordCalledFunction(Function.OnDetailsFetchAction)
    }

    override fun tagOnDetailsShareAction() {
        recordCalledFunction(Function.OnDetailsShareAction)
    }

    override fun tagOnShareAppNotFound() {
        recordCalledFunction(Function.OnDetailsShareNotFoundAction)
    }

    override fun tagOnError(error: String) {
        recordCalledFunction(Function.OnDetailsErrorEvent(message = error))
    }
}
