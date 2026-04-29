package com.cvs.aetna.search.fake

import com.cvs.aetna.search.analytics.CharacterNameAdobeTagUseCase

class FakeCharacterNameAdobeTagUseCase : CharacterNameAdobeTagUseCase {
    override fun tagOnClickImage(characterName: String) {
        // No-op for testing
    }

    override fun tagOnSearchScreenLoad() {
        // No-op for testing
    }

    override fun tagOnDetailsScreenLoad(characterName: String) {
        // No-op for testing
    }
}
