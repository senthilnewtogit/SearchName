package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.pub.R

@Composable
fun CharacterListItem(
    name: String?,
    imageUrl: String?,
    id: Int?,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val characterName = stringResource(R.string.ally_character_label)
    Column(
        modifier = modifier
            .padding(8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = characterName + name.orEmpty()
            }
            .clickable(
                onClick = {
                    onCharacterClick(id.toString())
                },

            ),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        with(sharedTransitionScope) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .semantics { hideFromAccessibility() }
                    .then(
                        if (animatedVisibilityScope == null) {
                            Modifier
                        } else {
                            Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "image/$id"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(durationMillis = 1000) },
                            )
                        },
                    ),
            )
            TextAuto(
                text = name.orEmpty().trim(),
                maxLines = 2,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .semantics { hideFromAccessibility() }
                    .then(
                        if (animatedVisibilityScope == null) {
                            Modifier
                        } else {
                            Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "text/$id"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(durationMillis = 1000) },
                            )
                        },
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterListItemPreview() {
    val characterDetails = CharacterDetails(
        name = "Rick Sanchez",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
    )
    val characterDetails1 = CharacterDetails(
        name = "Rick Sanchez",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
    )
    SharedTransitionLayout {
        CharacterListItem(
            name = characterDetails.name,
            imageUrl = characterDetails1.imageUrl,
            id = characterDetails1.id,
            onCharacterClick = {},
            modifier = Modifier,
            animatedVisibilityScope = null,
            sharedTransitionScope = this@SharedTransitionLayout,
        )
    }
}
