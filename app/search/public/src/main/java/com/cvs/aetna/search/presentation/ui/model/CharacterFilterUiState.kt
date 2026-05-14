package com.cvs.aetna.search.presentation.ui.model

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import com.cvs.aetna.search.domain.model.CharacterSearch
private const val NAME = "name"
private const val STATUS = "status"
private const val SPECIES = "species"
private const val TYPE = "type"

@Immutable
data class CharacterFilterUiState(
    val name: String = "",
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
)

fun CharacterFilterUiState.toDomain(): CharacterSearch = CharacterSearch(
    name = name,
    status = status,
    species = species,
    type = type,
)

fun SavedStateHandle.saveFilter(state: CharacterFilterUiState) {
    this[NAME] = state.name
    this[STATUS] = state.status
    this[SPECIES] = state.species
    this[TYPE] = state.type
}
fun SavedStateHandle.getFilter(): CharacterFilterUiState = CharacterFilterUiState(
    name = this[NAME] ?: "",
    status = this[STATUS],
    species = this[SPECIES],
    type = this[TYPE],
)
