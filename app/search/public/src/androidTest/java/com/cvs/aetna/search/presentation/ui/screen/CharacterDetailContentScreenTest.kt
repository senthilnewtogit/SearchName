package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cvs.aetna.search.presentation.ui.model.CharacterUIDetails
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDetailContentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val character = CharacterUIDetails(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        origin = "Earth",
        createdAt = "2017",
        imageUrl = "", // "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        hasError = false,
        errorMsg = null,
    )

    @Test
    fun givenCharacterDetails_whenScreenLoaded_thenShowCharacterInformation() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterDetailContentScreen(
                    character = character,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    shareOnClick = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertExists()

        composeTestRule
            .onNodeWithText("Status: Alive")
            .assertExists()

        composeTestRule
            .onNodeWithText("Species: Human")
            .assertExists()

        composeTestRule
            .onNodeWithText("Type: Scientist")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Share")
            .assertExists()
    }

    @Test
    fun givenShareIconWhenClickedThenInvokeShareCallback() {
        var isShareClicked = false

        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterDetailContentScreen(
                    character = character,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    shareOnClick = {
                        isShareClicked = true
                    },
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Share")
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(isShareClicked)
        }
    }

    @Test
    fun givenCharacterImageWhenScreenLoadedThenShowImage() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterDetailContentScreen(
                    character = character,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    shareOnClick = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Rick Sanchez")
            .assertExists()
    }

    @Test
    fun givenNullCharacterNameWhenScreenLoadedThenShowUnknown() {
        val characterWithNullName = character.copy(name = null)

        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterDetailContentScreen(
                    character = characterWithNullName,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    shareOnClick = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Unknown")
            .assertExists()
    }

    @Test
    fun givenCharacterAccessibilityLabelWhenScreenLoadedThenAccessibilityExists() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                CharacterDetailContentScreen(
                    character = character,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    shareOnClick = {},
                )
            }
        }

        composeTestRule
            .onNode(
                hasContentDescription(
                    "Character NameRick Sanchez",
                    substring = true,
                ),
            )
            .assertExists()
    }
}
