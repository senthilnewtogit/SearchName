package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cvs.aetna.search.presentation.ui.model.CharacterUIDetails
import com.cvs.aetna.search.presentation.ui.screen.components.ErrorUIElement
import com.cvs.aetna.search.presentation.ui.screen.components.LoadingIndicator
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsUiState
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsViewModel
import com.cvs.aetna.search.pub.R

@Composable
fun CharacterDetailsRouteScreen(
    id: String?,
    modifier: Modifier = Modifier,
) {
    val characterViewModel: CharacterDetailsViewModel = hiltViewModel<CharacterDetailsViewModel>()

    LaunchedEffect(id) {
        id?.let {
            characterViewModel.sendAction(CharacterDetailAction.FetchDetails(id))
        }
    }
    val characterState by characterViewModel.state.collectAsStateWithLifecycle()
    when (val state = characterState) {
        is CharacterDetailsUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LoadingIndicator()
            }
        }

        is CharacterDetailsUiState.Error -> {
            ErrorUIElement(
                error = state.message,
                modifier = modifier,
                onRetry = {
                    characterViewModel.sendAction(CharacterDetailAction.FetchDetails(id))
                },
            )
        }

        is CharacterDetailsUiState.Success -> {
            CharacterDetailContentScreen(character = state.characterDetails, modifier = modifier)
        }
    }
}

@Composable
fun CharacterDetailContentScreen(
    character: CharacterUIDetails,
    modifier: Modifier = Modifier,
) {
    val unknown = stringResource(R.string.character_details_unknown)
    val accessibilityLabel = stringResource(R.string.character_name)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .semantics {
                        hideFromAccessibility()
                    },
                contentScale = ContentScale.Crop,
            )
        }

        val characterName = character.name ?: unknown
        item {
            Text(
                text = characterName,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp).semantics {
                        contentDescription = accessibilityLabel + characterName
                    },
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(
                        R.string.character_details_species,
                        character.species ?: unknown,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = stringResource(
                        R.string.character_details_status,
                        character.status ?: unknown,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = stringResource(
                        R.string.character_details_origin,
                        character.origin ?: unknown,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )

                val charType = character.type
                if (!charType.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.character_details_type, charType),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                character.createdAt?.let { date ->
                    Text(
                        text = stringResource(R.string.character_details_created, date),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterDetailsScreenPreview() {
    val sampleCharacter = CharacterUIDetails(
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        origin = "Earth (C-137)",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        createdAt = "2017-11-04T18:48:46.250Z",
    )
    CharacterDetailContentScreen(character = sampleCharacter)
}
