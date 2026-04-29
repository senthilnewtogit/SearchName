package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cvs.aetna.search.domain.model.CharacterDetails

@Composable
fun CharacterGrid(
    list: List<CharacterDetails>,
    onCharacterClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            items = list,
            key = { it.id ?: it.name ?: it.hashCode() },
        ) { character ->
            CharacterItem(
                name = character.name,
                imageUrl = character.imageUrl,
                id = character.id,
                onCharacterClick = onCharacterClick,

            )
        }
    }
}
