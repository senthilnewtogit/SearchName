package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cvs.aetna.search.pub.R

private const val NO_RESULT_FOUND = "no_result_found"

@Composable
fun NoResultFound() {
    Box(
        modifier = Modifier.fillMaxSize().testTag(NO_RESULT_FOUND),
        contentAlignment = Alignment.Center,
    ) {
        TextAuto(text = stringResource(R.string.no_result_found))
    }
}

@Preview(showBackground = true)
@Composable
fun NoResultFoundPreview() {
    NoResultFound()
}
