package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.runtime.Composable
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.screen.components.FilterScreenUiContent

@Composable
fun FilterBottomSheetScreen(
    characterFilterUiState: CharacterFilterUiState,
    onDismiss: () -> Unit,
    onApply: (CharacterFilterUiState) -> Unit,
    onReset: () -> Unit,
) {
    FilterScreenUiContent(
        onDismiss = onDismiss,
        onApply = onApply,
        characterFilterUiState = characterFilterUiState,
        onReset = onReset,
    )
}
