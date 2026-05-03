package com.cvs.aetna.search.fake

import com.cvs.aetna.search.analytics.CharacterListAdobeTagUseCase

class FakeCharacterListAdobeTagUseCase :
    CharacterListAdobeTagUseCase,
    FakeFunctionHelper<FakeCharacterListAdobeTagUseCase.Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()

    sealed class Function {
        data class OnClickImage(val characterId: String) : Function()
        data object OnSearchScreenLoadEvent : Function()
        data class OnSearchAction(val characterName: String) : Function()
        data object OnFilterApplyAction : Function()
        data object OnFilterResetAction : Function()
        data object OnPageLoadMoreAction : Function()

        data class OnPageErrorEvent(val message: String?) : Function()
    }

    override fun tagOnClickImage(characterId: String) {
        recordCalledFunction(Function.OnClickImage(characterId = characterId))
    }

    override fun tagOnSearchScreenLoadEvent() {
        recordCalledFunction(Function.OnSearchScreenLoadEvent)
    }

    override fun tagOnSearchAction(characterName: String) {
        recordCalledFunction(Function.OnSearchAction(characterName = characterName))
    }

    override fun tagOnFilterApplyAction() {
        recordCalledFunction(Function.OnFilterApplyAction)
    }

    override fun tagOnFilterResetAction() {
        recordCalledFunction(Function.OnFilterResetAction)
    }

    override fun tagOnPageLoadMoreAction() {
        recordCalledFunction(Function.OnPageLoadMoreAction)
    }

    override fun tagOnPageErrorEvent(message: String?) {
        recordCalledFunction(Function.OnPageErrorEvent(message = message))
    }
}
