package com.cvs.aetna.search.presentation.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class CharacterUIDetails(
    val id: Int? = null,
    val name: String? = null,
    val status: String? = null,
    val origin: String? = null,
    val species: String? = null,
    val type: String? = null,
    val createdAt: String? = null,
    val imageUrl: String? = null,
    val hasError: Boolean = false,
    val errorMsg: String? = null,
)
