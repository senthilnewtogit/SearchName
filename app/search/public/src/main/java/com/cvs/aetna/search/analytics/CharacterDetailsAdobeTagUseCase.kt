package com.cvs.aetna.search.analytics

interface CharacterDetailsAdobeTagUseCase {
    fun tagOnDetailsScreenLoad()

    fun tagOnDetailsFetchAction()

    fun tagOnDetailsShareAction()

    fun tagOnShareAppNotFound()

    fun tagOnError(error: String)
}
