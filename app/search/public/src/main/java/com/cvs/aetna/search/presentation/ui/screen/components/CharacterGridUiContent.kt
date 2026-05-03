package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cvs.aetna.search.presentation.ui.model.CharacterListUiModel
import com.cvs.aetna.search.pub.R
import kotlinx.coroutines.flow.distinctUntilChanged

private const val TEST_TAG_CHARACTER_LIST = "character_list"

private const val LOAD_MORE_THRESHOLD = 3

@Composable
fun CharacterListGridUiContent(
    list: List<CharacterListUiModel>,
    onCharacterClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onEndOfList: () -> Unit,
    shouldLoadMore: Boolean,
    isLoadingMore: Boolean,
    totalCount: Int,
) {
    val listState = rememberLazyGridState()

    CheckEndOfList(listState, shouldLoadMore, onEndOfList, list)
    TextAuto(
        text = stringResource(R.string.character_count_label, totalCount),
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp).semantics {
                heading()
            },
    )
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize().testTag(TEST_TAG_CHARACTER_LIST),
        state = listState,
    ) {
        items(
            items = list,
            key = { it.id ?: it.name ?: "" },
        ) { character ->
            CharacterListItem(
                name = character.name,
                imageUrl = character.imageUrl,
                id = character.id,
                onCharacterClick = onCharacterClick,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,

            )
        }
        if (isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CheckEndOfList(
    listState: LazyGridState,
    shouldLoadMore: Boolean,
    onEndOfList: () -> Unit,
    list: List<CharacterListUiModel>,
) {
    var isLoadingMore by remember { mutableStateOf(false) }

    LaunchedEffect(listState, shouldLoadMore) {
        if (!shouldLoadMore) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = layoutInfo.totalItemsCount
            if (total == 0) return@snapshotFlow false
            lastVisible >= total - LOAD_MORE_THRESHOLD
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->

                if (isNearEnd && !isLoadingMore) {
                    isLoadingMore = true
                    onEndOfList()
                }
            }
    }

    LaunchedEffect(list.size) {
        isLoadingMore = false
    }
}
