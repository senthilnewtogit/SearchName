package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvs.aetna.search.presentation.ui.model.UiText
import com.cvs.aetna.search.pub.R

@Composable
fun ErrorUIElement(
    modifier: Modifier = Modifier,
    error: String? = null,
    uiText: UiText? = null,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextAuto(
            text = if (uiText is UiText.StringResource) {
                stringResource(id = uiText.resId)
            } else {
                error
                    ?: stringResource(R.string.unknown_error)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .testTag("retry_button")
                .semantics(mergeDescendants = true) {},
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
                    .semantics {
                        hideFromAccessibility()
                    },
            )
            TextAuto(text = stringResource(R.string.retry))
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ErrorUIElementPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        ErrorUIElement(
            error = "An error occurred while fetching data.",
            onRetry = { /* No-op for preview */ },
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
