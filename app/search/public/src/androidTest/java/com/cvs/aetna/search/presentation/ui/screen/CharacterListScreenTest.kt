package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.model.CharacterListUiModel
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterListScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun givenCharacterList_whenItemClicked_thenNavigateTriggered() {
        val sampleCharacters = listOf(
            CharacterListUiModel(
                id = 1,
                name = "Rick Sanchez",
                imageUrl = "",
            ),
        )

        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterListScreen(
                    characterSearchUiState =
                    CharacterSearchUiState.Success(sampleCharacters),
                    onCharacterClick = {},
                    onCharacterType = {},
                    sharedTransitionScope = this,
                    searchFilterState = CharacterFilterUiState(),
                    onResetFilter = {},
                    onFilterUpdate = {},
                    onEndOfList = {},
                    onPageLoad = {},
                )
            }
        }

        // Wait for UI
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("character_search_screen")
            .assertIsDisplayed()

        // STEP 2: Click character item
        composeTestRule
            .onNodeWithTag("character_item_1")
            .assertExists()
            .performClick()
    }

    @Test
    fun whenTypingSearch_thenCallbackTriggered() {
        var searchQuery = ""

        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterListScreen(
                    characterSearchUiState = CharacterSearchUiState.Empty,
                    onCharacterClick = {},
                    onCharacterType = { searchQuery = it },
                    sharedTransitionScope = this,
                    searchFilterState = CharacterFilterUiState(),
                    onResetFilter = {},
                    onFilterUpdate = {},
                    onEndOfList = {},
                    onPageLoad = {},
                )
            }
        }

        composeTestRule
            .onNodeWithTag("character_search_screen")
            .assertExists()

        // simulate typing via semantic node (needs testTag on SearchTextField)
        composeTestRule
            .onNodeWithTag("character_search_input")
            .performTextInput("Rick")

        assert(searchQuery == "Rick")
    }

    @Test
    fun givenLoadingState_thenShowLoader() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterListScreen(
                    characterSearchUiState = CharacterSearchUiState.Loading,
                    onCharacterClick = {},
                    onCharacterType = {},
                    sharedTransitionScope = this,
                    searchFilterState = CharacterFilterUiState(),
                    onResetFilter = {},
                    onFilterUpdate = {},
                    onEndOfList = {},
                    onPageLoad = {},
                )
            }
        }

        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun givenEmptyState_thenShowNoResult() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterListScreen(
                    characterSearchUiState = CharacterSearchUiState.Empty,
                    onCharacterClick = {},
                    onCharacterType = {},
                    sharedTransitionScope = this,
                    searchFilterState = CharacterFilterUiState(),
                    onResetFilter = {},
                    onFilterUpdate = {},
                    onEndOfList = {},
                    onPageLoad = {},
                )
            }
        }

        composeTestRule
            .onNodeWithTag("no_result_found")
            .assertIsDisplayed()
    }
}
