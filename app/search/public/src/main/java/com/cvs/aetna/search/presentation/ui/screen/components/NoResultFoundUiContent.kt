package com.cvs.aetna.search.presentation.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cvs.aetna.search.pub.R

@Composable
fun NoResultFound() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.no_result_found))
    }
}

@Preview(showBackground = true)
@Composable
fun NoResultFoundPreview() {
    NoResultFound()
}
