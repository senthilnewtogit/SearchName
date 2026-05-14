package com.cvs.aetna.search.usecase.analytics

import com.cvs.aetna.search.analytics.CharacterListAdobeTagUseCase
import javax.inject.Inject

class DefaultCharacterListAdobeTagUseCase @Inject constructor() : CharacterListAdobeTagUseCase {
    override fun tagOnClickImage(characterId: String) {
    }

    override fun tagOnSearchScreenLoadEvent() {
    }

    override fun tagOnSearchAction(characterName: String) {
    }

    override fun tagOnFilterApplyAction() {
    }

    override fun tagOnFilterResetAction() {
    }

    override fun tagOnPageLoadMoreAction() {
    }

    override fun tagOnPageErrorEvent(message: String?) {
    }
}
