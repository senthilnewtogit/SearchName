package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.screen.components.UiBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterBottomSheetUiBodyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenInitialState_whenScreenLoaded_thenShowAllUiElements() {
        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(),
                    onApply = {},
                    onReset = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Filter Characters")
            .assertExists()

        composeTestRule
            .onNodeWithText("Reset")
            .assertExists()

        composeTestRule
            .onNodeWithText("Apply")
            .assertExists()

        composeTestRule
            .onNodeWithText("Human")
            .assertExists()

        composeTestRule
            .onNodeWithText("Alive")
            .assertExists()

        composeTestRule
            .onNode(hasSetTextAction())
            .assertExists()
    }

    @Test
    fun givenStatusChip_whenClicked_thenChipSelected() {
        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(),
                    onApply = {},
                    onReset = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Alive")
            .performClick()

        composeTestRule
            .onNodeWithTag("status_chip_Alive")
            .assertIsSelected()
    }

    @Test
    fun givenSpeciesChip_whenClicked_thenSpeciesSelected() {
        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(),
                    onApply = {},
                    onReset = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Human")
            .performClick()

        composeTestRule
            .onNodeWithTag("species_chip_Human")
            .assertIsSelected()
    }

    @Test
    fun givenTypeInput_whenTyping_thenValueUpdated() {
        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(),
                    onApply = {},
                    onReset = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Alien")

        composeTestRule
            .onNode(hasSetTextAction())
            .assertTextContains("Alien")
    }

    @Test
    fun givenSelectedFilters_whenApplyClicked_thenReturnSelectedState() {
        var appliedState: CharacterFilterUiState? = null
        var dismissCalled = false

        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(
                        name = "Rick",
                    ),
                    onApply = {
                        appliedState = it
                    },
                    onReset = {},
                    onDismiss = {
                        dismissCalled = true
                    },
                )
            }
        }

        composeTestRule
            .onNodeWithText("Alive")
            .performClick()

        composeTestRule
            .onNodeWithText("Human")
            .performClick()

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Leader")

        composeTestRule
            .onNodeWithText("Apply")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                CharacterFilterUiState(
                    name = "Rick",
                    status = "Alive",
                    species = "Human",
                    type = "Leader",
                ),
                appliedState,
            )

            assertTrue(dismissCalled)
        }
    }

    @Test
    fun givenSelectedFilters_whenResetClicked_thenResetAndDismissCalled() {
        var resetCalled = false
        var dismissCalled = false

        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(
                        status = "Alive",
                        species = "Human",
                        type = "Leader",
                    ),
                    onApply = {},
                    onReset = {
                        resetCalled = true
                    },
                    onDismiss = {
                        dismissCalled = true
                    },
                )
            }
        }

        composeTestRule
            .onNodeWithText("Reset")
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(resetCalled)
            assertTrue(dismissCalled)
        }
    }

    @Test
    fun givenAlreadySelectedChip_whenClickedAgain_thenDeselected() {
        composeTestRule.setContent {
            MaterialTheme {
                UiBody(
                    characterFilterUiState = CharacterFilterUiState(),
                    onApply = {},
                    onReset = {},
                    onDismiss = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Alive")
            .performClick()

        composeTestRule
            .onNodeWithText("Alive")
            .performClick()

        composeTestRule
            .onNodeWithTag("status_chip_Alive")
            .assertIsNotSelected()
    }
}
