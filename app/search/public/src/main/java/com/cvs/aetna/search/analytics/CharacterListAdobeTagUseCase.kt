package com.cvs.aetna.search.analytics

interface CharacterListAdobeTagUseCase {
    fun tagOnClickImage(characterId: String)
    fun tagOnSearchScreenLoadEvent()
    fun tagOnSearchAction(characterName: String)

    fun tagOnFilterApplyAction()

    fun tagOnFilterResetAction()

    fun tagOnPageLoadMoreAction()

    fun tagOnPageErrorEvent(message: String?)
}
