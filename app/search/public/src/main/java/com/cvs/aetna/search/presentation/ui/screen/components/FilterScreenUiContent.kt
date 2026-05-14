package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.pub.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenUiContent(
    characterFilterUiState: CharacterFilterUiState,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    onApply: (CharacterFilterUiState) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize(),
        shape = BottomSheetDefaults.ExpandedShape,
        contentWindowInsets = {
            WindowInsets.safeDrawing
        },

    ) {
        UiBody(
            characterFilterUiState = characterFilterUiState,
            onApply = onApply,
            onReset = onReset,
            onDismiss = onDismiss,
        )
    }
}

private const val SPECIES_TEST_TAG = "species_chip_"

private const val STATUS_TEST_TAG = "status_chip_"

@Composable
fun UiBody(
    characterFilterUiState: CharacterFilterUiState,
    onApply: (CharacterFilterUiState) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    var status by rememberSaveable { mutableStateOf(characterFilterUiState.status) }
    var species by rememberSaveable { mutableStateOf(characterFilterUiState.species) }
    var type by rememberSaveable {
        mutableStateOf(
            characterFilterUiState.type ?: "",
        )
    }

    val allyStatusLabel = stringResource(R.string.a11y_status_filter_label)
    val allySpeciesLabel = stringResource(R.string.a11y_species_filter_label)
    val notSelectedLabel = stringResource(R.string.a11y_not_selected_label)
    val selectedLabel = stringResource(R.string.a11y_selected_label)
    val speciesList = stringArrayResource(id = R.array.species_list)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.filter_characters),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },

            )

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {},
            ) {
                Text(
                    stringResource(R.string.filter_status),
                    style = MaterialTheme.typography.labelLarge,
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    stringArrayResource(R.array.status_list).forEach { item ->
                        FilterChip(
                            selected = status == item,
                            onClick = { status = if (status == item) null else item },
                            label = { Text(item) },
                            Modifier.semantics {
                                stateDescription =
                                    "$allyStatusLabel $item ${if (status == item) selectedLabel else notSelectedLabel}"
                            }.testTag("$STATUS_TEST_TAG$item"),

                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {},
            ) {
                Text(stringResource(R.string.species), style = MaterialTheme.typography.labelLarge)

                FlowRow(
                    maxLines = 2,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    speciesList.forEach { item ->
                        FilterChip(
                            selected = species == item,
                            onClick = {
                                species = if (species == item) null else item
                            },
                            label = { Text(item) },
                            Modifier.semantics {
                                stateDescription =
                                    "$allySpeciesLabel $item ${if (species == item) selectedLabel else notSelectedLabel}"
                            }.testTag("$SPECIES_TEST_TAG$item"),
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Column {
                Text(
                    stringResource(R.string.type),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.semantics {
                        hideFromAccessibility()
                    },
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.enter_type)) },
                )
            }
            Spacer(Modifier.height(24.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = {
                    status = null
                    species = null
                    type = ""
                    onReset()
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.reset))
            }

            Button(
                onClick = {
                    val filterUiState = CharacterFilterUiState(
                        name = characterFilterUiState.name,
                        status = status,
                        species = species,
                        type = type.ifEmpty { null },
                    )
                    onApply(filterUiState)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.apply))
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    UiBody(
        characterFilterUiState = CharacterFilterUiState(),
        onApply = {},
        onReset = {},
        onDismiss = {},
    )
}
