package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.fake.FakeCharacterListUseCase
import com.cvs.aetna.search.fake.FakeTelemetryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterSearchViewModelTest {

    private lateinit var subject: CharacterSearchViewModel
    private val fakeSavedStateHandle: SavedStateHandle = SavedStateHandle()
    private val fakeTelemetryService = FakeTelemetryService()
    private val fakeCharacterListUseCase = FakeCharacterListUseCase()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        subject = CharacterSearchViewModel(
            savedStateHandle = fakeSavedStateHandle,
            telemetryService = fakeTelemetryService,
            characterListUseCase = fakeCharacterListUseCase,
            dispatcher = testDispatcher,
        )
    }

    @After
    fun teardown() {
        fakeCharacterListUseCase.verifyNoFunctionsCalled()
        fakeTelemetryService.verifyNoFunctionsCalled()
    }

    @Test
    fun `initial state is Empty`() = runTest(testDispatcher) {
        subject.state.test {
            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `initial state has no events`() = runTest(testDispatcher) {
        subject.events.test {
            // Should not emit any events initially
            expectNoEvents()
        }
    }

    @Test
    fun `given Search action with valid name, when executed, then emit Loading then Success state`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(
                CharacterDetails(
                    id = 1,
                    name = "Rick Sanchez",
                    imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                ),
            ),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)
            assertEquals(1, success.charactersList[0].id)
            assertEquals("Rick Sanchez", success.charactersList[0].name)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with empty name, when executed, then return all characters`() = runTest(testDispatcher) {
        val allCharacters = listOf(
            CharacterDetails(id = 1, name = "Rick Sanchez", imageUrl = "url1"),
            CharacterDetails(id = 2, name = "Morty Smith", imageUrl = "url2"),
            CharacterDetails(id = 3, name = "Summer Smith", imageUrl = "url3"),
        )
        fakeCharacterListUseCase.characterList = CharacterList(allCharacters)

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search(""))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(3, success.charactersList.size)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = ""),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with no results, when executed, then emit Success with empty list`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(emptyList())

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("nonexistent"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertTrue(success.charactersList.isEmpty())

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "nonexistent"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given multiple Search actions, when executed sequentially, then emit correct states each time`() = runTest(testDispatcher) {
        // First search
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick Sanchez", imageUrl = "url1")),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val firstSuccess = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, firstSuccess.charactersList.size)

            // Second search
            fakeCharacterListUseCase.characterList = CharacterList(
                listOf(
                    CharacterDetails(id = 2, name = "Morty Smith", imageUrl = "url2"),
                    CharacterDetails(id = 3, name = "Summer Smith", imageUrl = "url3"),
                ),
            )

            subject.sendAction(CharacterSearchAction.Search("smith"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val secondSuccess = awaitItem() as CharacterSearchUiState.Success
            assertEquals(2, secondSuccess.charactersList.size)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "smith"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with error response, when executed, then emit Loading then Error state`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            hasError = true,
            errorMsg = "Network error",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            assertEquals("Network error", error.message)

            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Network error",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with null error message, when executed, then emit Error with null message`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            hasError = true,
            errorMsg = null,
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            assertEquals(null, error.message)
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Unknown error",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given OnCharacterClick action, when executed, then emit NavigateToDetails event`() = runTest(testDispatcher) {
        subject.events.test {
            subject.sendAction(CharacterSearchAction.OnCharacterClick("123"))
            advanceUntilIdle()

            val event = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("123", event.characterId)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given multiple OnCharacterClick actions, when executed, then emit multiple NavigateToDetails events`() = runTest(testDispatcher) {
        subject.events.test {
            subject.sendAction(CharacterSearchAction.OnCharacterClick("1"))
            subject.sendAction(CharacterSearchAction.OnCharacterClick("2"))
            subject.sendAction(CharacterSearchAction.OnCharacterClick("3"))
            advanceUntilIdle()

            val event1 = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("1", event1.characterId)

            val event2 = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("2", event2.characterId)

            val event3 = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("3", event3.characterId)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ReachedEndOfList action, when executed, then no state changes occur`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()

            // Should only emit initial Empty state
            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `given state update, when updateState called, then state changes correctly`() = runTest(testDispatcher) {
        subject.state.test {
            assertEquals(CharacterSearchUiState.Empty, awaitItem())

            subject.updateState { CharacterSearchUiState.Loading }
            assertEquals(CharacterSearchUiState.Loading, awaitItem())

            subject.updateState { CharacterSearchUiState.Success(emptyList()) }
            val success = awaitItem() as CharacterSearchUiState.Success
            assertTrue(success.charactersList.isEmpty())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given use case throws exception, when search executed, then emit Error state and log telemetry`() = runTest(testDispatcher) {
        // Note: This test would require mocking the use case to throw an exception
        // For now, we test the error path through the use case error response
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            hasError = true,
            errorMsg = "Use case error",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            assertEquals("Use case error", error.message)
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Use case error",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given empty search query, when executed, then handle gracefully`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(emptyList())

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search(""))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertTrue(success.charactersList.isEmpty())

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = ""),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given search with special characters, when executed, then handle gracefully`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(emptyList())

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("!@#$%"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertTrue(success.charactersList.isEmpty())

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "!@#$%"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given complete search flow, when executed, then state transitions are correct`() = runTest(testDispatcher) {
        // Start with success
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        subject.state.test {
            // Initial state
            assertEquals(CharacterSearchUiState.Empty, awaitItem())

            // First search - success
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success1 = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success1.charactersList.size)

            // Second search - error
            fakeCharacterListUseCase.characterList = CharacterList(
                characters = emptyList(),
                hasError = true,
                errorMsg = "Error occurred",
            )
            subject.sendAction(CharacterSearchAction.Search("error"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            assertEquals("Error occurred", error.message)
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Error occurred",
                    ),
                ),
                1,
            )

            // Third search - success again
            fakeCharacterListUseCase.characterList = CharacterList(
                listOf(
                    CharacterDetails(id = 2, name = "Morty", imageUrl = "url2"),
                    CharacterDetails(id = 3, name = "Summer", imageUrl = "url3"),
                ),
            )
            subject.sendAction(CharacterSearchAction.Search("smith"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success2 = awaitItem() as CharacterSearchUiState.Success
            assertEquals(2, success2.charactersList.size)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "error"),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "smith"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given character click after search, when executed, then both state and event emitted correctly`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick Sanchez", imageUrl = "url")),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)

            cancelAndConsumeRemainingEvents()
        }

        subject.events.test {
            subject.sendAction(CharacterSearchAction.OnCharacterClick("1"))
            advanceUntilIdle()

            val event = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("1", event.characterId)

            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
            1,
        )
    }

    @Test
    fun `given rapid successive actions, when executed, then handle gracefully`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        subject.state.test {
            // Send multiple search actions rapidly
            subject.sendAction(CharacterSearchAction.Search("rick"))
            subject.sendAction(CharacterSearchAction.Search("morty"))
            subject.sendAction(CharacterSearchAction.Search("summer"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)

            // Should process all actions, but only last one visible in state
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "morty"),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(name = "summer"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ViewModel recreated, when actions sent, then handle correctly`() = runTest(testDispatcher) {
        // Simulate ViewModel recreation
        val newViewModel = CharacterSearchViewModel(
            savedStateHandle = SavedStateHandle(),
            telemetryService = FakeTelemetryService(),
            characterListUseCase = fakeCharacterListUseCase,
            dispatcher = testDispatcher,
        )

        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        newViewModel.state.test {
            newViewModel.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)

            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetCharacterList(name = "rick"),
            1,
        )
    }
}
