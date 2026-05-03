package com.cvs.aetna.search.presentation.ui.model

import androidx.compose.runtime.Immutable
import com.cvs.aetna.search.domain.model.CharacterDetails

@Immutable
data class CharacterListUiModel(
    val id: Int? = null,
    val name: String? = null,
    val imageUrl: String? = null,
)
fun List<CharacterDetails>.toUiModelList(): List<CharacterListUiModel> = map { it.toUiModel() }
fun CharacterDetails.toUiModel(): CharacterListUiModel = CharacterListUiModel(
    id = id,
    name = name?.trim().orEmpty(),
    imageUrl = imageUrl.orEmpty(),
)
