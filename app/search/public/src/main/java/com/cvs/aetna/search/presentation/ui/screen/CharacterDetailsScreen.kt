package com.cvs.aetna.search.presentation.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cvs.aetna.search.domain.model.ShareData
import com.cvs.aetna.search.presentation.ui.model.CharacterUIDetails
import com.cvs.aetna.search.presentation.ui.screen.components.ErrorUIElement
import com.cvs.aetna.search.presentation.ui.screen.components.LoadingIndicator
import com.cvs.aetna.search.presentation.ui.screen.components.OnPageLoad
import com.cvs.aetna.search.presentation.ui.screen.components.TextAuto
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsEvent
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsUiState
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsViewModel
import com.cvs.aetna.search.presentation.viewmodel.NO_APP_FOUND
import com.cvs.aetna.search.presentation.viewmodel.NO_SHARE_DATA_FOUND
import com.cvs.aetna.search.pub.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CharacterDetailsRouteScreen(
    id: String?,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel(),
    shareOnClick: () -> Unit,
    onPageLoad: () -> Unit,
) {
    OnPageLoad(onPageLoad = onPageLoad)
    FetchCharacterDetails(id, characterDetailsViewModel)
    HandleEvent(characterDetailsViewModel)
    val characterState by characterDetailsViewModel.state.collectAsStateWithLifecycle()
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
                    characterDetailsViewModel.sendAction(CharacterDetailAction.FetchDetails(id))
                },
            )
        }

        is CharacterDetailsUiState.Success -> {
            CharacterDetailContentScreen(
                character = state.characterDetails,
                modifier = modifier,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                shareOnClick = shareOnClick,
            )
        }
    }
}

@Composable
private fun HandleEvent(characterDetailsViewModel: CharacterDetailsViewModel) {
    val context = LocalContext.current
    val title = stringResource(R.string.share_character)
    LaunchedEffect(Unit) {
        characterDetailsViewModel.events.collectLatest { event ->
            when (event) {
                is CharacterDetailsEvent.ShareImageData -> {
                    startChooserActivity(event.shareData, title, context, characterDetailsViewModel)
                }

                is CharacterDetailsEvent.ShowError -> {
                    when (event.message) {
                        NO_APP_FOUND -> {
                            Toast.makeText(context, R.string.no_share_app_found, Toast.LENGTH_SHORT).show()
                        }
                        NO_SHARE_DATA_FOUND -> {
                            Toast.makeText(context, R.string.no_share_data_found, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FetchCharacterDetails(
    id: String?,
    characterDetailsViewModel: CharacterDetailsViewModel,
) {
    LaunchedEffect(id) {
        id?.let {
            characterDetailsViewModel.sendAction(CharacterDetailAction.FetchDetails(id))
        }
    }
}

private fun startChooserActivity(
    shareData: ShareData,
    title: String,
    context: Context,
    characterDetailsViewModel: CharacterDetailsViewModel,
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, shareData.fileUri.toUri())
        putExtra(Intent.EXTRA_TEXT, shareData.shareText)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val createChooser =
        Intent.createChooser(intent, title)
    if (createChooser.resolveActivity(context.packageManager) != null) {
        context.startActivity(createChooser)
    } else {
        characterDetailsViewModel.sendAction(CharacterDetailAction.ShareAppNotFound)
    }
}

@Composable
fun CharacterDetailContentScreen(
    character: CharacterUIDetails,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope,
    shareOnClick: () -> Unit,
) {
    val unknown = stringResource(R.string.character_details_unknown)
    val accessibilityLabel = stringResource(R.string.ally_character_label)
    val characterName = character.name ?: unknown
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),

        ) {
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val imageModifier = if (isPortrait) {
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .semantics {
                        hideFromAccessibility()
                    }
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .semantics {
                        hideFromAccessibility()
                    }
            }
            with(sharedTransitionScope) {
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = character.name,
                    modifier = imageModifier.then(
                        if (animatedVisibilityScope == null) {
                            Modifier
                        } else {
                            Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "image/${character.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(durationMillis = 1000) },
                            )
                        },
                    ),
                    contentScale = if (isPortrait) ContentScale.Crop else ContentScale.FillWidth,
                )
                TextAuto(
                    text = characterName,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics {
                            contentDescription = accessibilityLabel + characterName
                        }
                        .then(
                            if (animatedVisibilityScope == null) {
                                Modifier
                            } else {
                                Modifier.sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "text/${character.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(durationMillis = 1000) },
                                )
                            },
                        ),
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            CharacterDetailsUiContent(character, unknown)
        }

        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = stringResource(R.string.share),
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(32.dp)
                .clickable {
                    shareOnClick()
                },
        )
    }
}

@Composable
private fun CharacterDetailsUiContent(
    character: CharacterUIDetails,
    unknown: String,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        TextAuto(
            text = stringResource(
                R.string.character_details_species,
                character.species ?: unknown,
            ),
            style = MaterialTheme.typography.bodyLarge,

        )

        TextAuto(
            text = stringResource(
                R.string.character_details_status,
                character.status ?: unknown,
            ),
            style = MaterialTheme.typography.bodyLarge,
        )

        TextAuto(
            text = stringResource(
                R.string.character_details_origin,
                character.origin ?: unknown,
            ),
            style = MaterialTheme.typography.bodyLarge,

        )

        val charType = character.type
        if (!charType.isNullOrBlank()) {
            TextAuto(
                text = stringResource(R.string.character_details_type, charType),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        character.createdAt?.let { date ->
            TextAuto(
                text = stringResource(R.string.character_details_created, date),
                style = MaterialTheme.typography.bodyMedium,
            )
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
    SharedTransitionLayout {
        CharacterDetailContentScreen(
            character = sampleCharacter,
            sharedTransitionScope = this@SharedTransitionLayout,
            shareOnClick = {},
        )
    }
}
