package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvs.aetna.search.pub.R

private const val FILTER_TEST_TAG = "filter_button"

private const val TEST_TAG_SEARCH_INPUT = "search_input"

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search characters...",
    isFilterApplied: Boolean = false,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.testTag(TEST_TAG_SEARCH_INPUT)
                .fillMaxWidth()
                .padding(16.dp).semantics(mergeDescendants = true) {
                },
            placeholder = { TextAuto(text = placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.semantics {
                        hideFromAccessibility()
                    },
                )
            },
            trailingIcon = {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter),
                        modifier = Modifier.testTag(FILTER_TEST_TAG),
                        tint = if (isFilterApplied) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            },
            singleLine = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchTextFieldPreview() {
    SearchTextField(
        value = "",
        onValueChange = {},
        onFilterClick = {},
    )
}
