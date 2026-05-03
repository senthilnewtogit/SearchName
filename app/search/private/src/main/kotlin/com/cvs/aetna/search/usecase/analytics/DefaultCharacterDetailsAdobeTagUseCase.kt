package com.cvs.aetna.search.usecase.analytics

import com.cvs.aetna.search.analytics.CharacterDetailsAdobeTagUseCase
import javax.inject.Inject

class DefaultCharacterDetailsAdobeTagUseCase@Inject constructor() : CharacterDetailsAdobeTagUseCase {
    override fun tagOnDetailsScreenLoad() {
    }

    override fun tagOnDetailsFetchAction() {
    }

    override fun tagOnDetailsShareAction() {
    }

    override fun tagOnShareAppNotFound() {
    }

    override fun tagOnError(error: String) {
    }
}
