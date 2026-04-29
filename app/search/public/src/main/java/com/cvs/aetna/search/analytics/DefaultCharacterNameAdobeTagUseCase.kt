package com.cvs.aetna.search.analytics

import javax.inject.Inject

interface CharacterNameAdobeTagUseCase {
    fun tagOnClickImage(characterName: String)
    fun tagOnSearchScreenLoad()
    fun tagOnDetailsScreenLoad(characterName: String)
}
class DefaultCharacterNameAdobeTagUseCase @Inject constructor() : CharacterNameAdobeTagUseCase {
    override fun tagOnClickImage(characterName: String) {
    }

    override fun tagOnSearchScreenLoad() {
    }

    override fun tagOnDetailsScreenLoad(characterName: String) {
    }
}
