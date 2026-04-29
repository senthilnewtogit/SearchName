package com.cvs.aetna.task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.cvs.aetna.search.presentation.navigation.CharacterNavGraph
import com.cvs.aetna.task.ui.theme.AetnaInterviewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchNameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetnaInterviewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CharacterNavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
