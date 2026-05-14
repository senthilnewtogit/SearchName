package com.cvs.aetna.search.presentation.ui.model

import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.pub.R

sealed interface UiText {
    data class StringResource(val resId: Int) : UiText
    data class Dynamic(val value: String) : UiText
}

fun CharacterError.toUiText(): UiText = when (this) {
    is CharacterError.NetworkIO -> UiText.StringResource(resId = R.string.error_network)
    is CharacterError.NoInternet -> UiText.StringResource(resId = R.string.error_no_internet)
    is CharacterError.NoResultFound -> UiText.StringResource(resId = R.string.error_no_result)
    is CharacterError.Parsing -> UiText.StringResource(resId = R.string.error_parsing)
    is CharacterError.SSL -> UiText.StringResource(resId = R.string.error_ssl)
    is CharacterError.Timeout -> UiText.StringResource(resId = R.string.error_timeout)
    is CharacterError.Unknown -> UiText.StringResource(resId = R.string.error_unknown)
    is CharacterError.UnknownHost -> UiText.StringResource(resId = R.string.error_no_internet)
    is CharacterError.Http -> when (code) {
        429 -> UiText.StringResource(resId = R.string.error_rate_limit)
        in 400..499 -> UiText.StringResource(resId = R.string.error_client)
        in 500..599 -> UiText.StringResource(resId = R.string.error_server)
        else -> UiText.StringResource(resId = R.string.error_unknown)
    }
    is CharacterError.RateLimit -> UiText.StringResource(resId = R.string.error_rate_limit)
}
fun UiText.asStringRes(): Int = (this as UiText.StringResource).resId
