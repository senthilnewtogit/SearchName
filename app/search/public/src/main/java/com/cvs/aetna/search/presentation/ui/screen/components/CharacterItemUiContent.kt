package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun CharacterItem(
    name: String?,
    imageUrl: String?,
    id: Int?,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val characterName = stringResource(R.string.character_name)
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
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .semantics { hideFromAccessibility() },
        )
        Text(
            text = name.orEmpty().trim(),
            maxLines = 2,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            modifier = Modifier.semantics { hideFromAccessibility() },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterItemPreview() {
    val characterDetails = CharacterDetails(
        name = "Rick Sanchez",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
    )
    val characterDetails1 = CharacterDetails(
        name = "Rick Sanchez",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
    )
    CharacterItem(
        name = characterDetails.name,
        imageUrl = characterDetails1.imageUrl,
        id = characterDetails1.id,
        onCharacterClick = {},
        modifier = Modifier,
    )
}
