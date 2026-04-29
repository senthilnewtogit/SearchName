package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.presentation.ui.screen.components.CharacterGrid
import com.cvs.aetna.search.presentation.ui.screen.components.ErrorUIElement
import com.cvs.aetna.search.presentation.ui.screen.components.LoadingIndicator
import com.cvs.aetna.search.presentation.ui.screen.components.NoResultFound
import com.cvs.aetna.search.presentation.ui.screen.components.SearchTextField
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchUiState

@Composable
fun CharacterListScreen(
    characterSearchUiState: CharacterSearchUiState,
    onCharacterClick: (String) -> Unit,
    onCharacterType: (String) -> Unit,
) {
    val characterList = remember { mutableStateOf(emptyList<CharacterDetails>()) }
    when (characterSearchUiState) {
        CharacterSearchUiState.Empty -> {
        }

        is CharacterSearchUiState.Error -> {
        }

        CharacterSearchUiState.Loading -> {
        }

        is CharacterSearchUiState.Success -> {
            characterList.value = characterSearchUiState.charactersList
        }
    }
    ShowListScreen(
        characterList = characterList.value,
        onCharacterClick = onCharacterClick,
        onCharacterType = onCharacterType,
        characterSearchUiState = characterSearchUiState,
    )
}

@Composable
private fun ShowListScreen(
    characterList: List<CharacterDetails>,
    onCharacterType: (String) -> Unit,
    onCharacterClick: (String) -> Unit,
    characterSearchUiState: CharacterSearchUiState,
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchTextField(
            value = searchQuery,
            onValueChange = {
                onCharacterType(it)
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
        )
        when (characterSearchUiState) {
            CharacterSearchUiState.Loading -> {
                LoadingIndicator()
            }
            is CharacterSearchUiState.Error -> {
                ErrorUIElement(error = characterSearchUiState.message, onRetry = {
                    onCharacterType(searchQuery)
                })
            }

            else -> {
                ShowCharacterList(characterList = characterList, onCharacterClick = onCharacterClick)
            }
        }
    }
}

@Composable
fun ShowCharacterList(characterList: List<CharacterDetails>, onCharacterClick: (String) -> Unit) {
    if (characterList.isEmpty()) {
        NoResultFound()
    } else {
        CharacterGrid(
            list = characterList,
            onCharacterClick = onCharacterClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterListScreenPreview() {
    val sampleCharacters = listOf(
        CharacterDetails(id = 1, name = "Rick Sanchez", imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"),
        CharacterDetails(id = 2, name = "Morty Smith", imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"),
    )
    CharacterListScreen(
        characterSearchUiState = CharacterSearchUiState.Success(sampleCharacters),
        onCharacterClick = {},
        onCharacterType = {},
    )
}
