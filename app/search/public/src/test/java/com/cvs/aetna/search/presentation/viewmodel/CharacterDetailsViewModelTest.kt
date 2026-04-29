package com.cvs.aetna.search.presentation.viewmodel

import app.cash.turbine.test
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.fake.FakeCharacterDetailsUseCase
import com.cvs.aetna.search.fake.FakeTelemetryService
import com.cvs.aetna.search.presentation.ui.model.CharacterUIDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailsViewModelTest {

    private lateinit var subject: CharacterDetailsViewModel
    private val fakeTelemetryService = FakeTelemetryService()
    private val fakeCharacterDetailsUseCase = FakeCharacterDetailsUseCase()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        subject = CharacterDetailsViewModel(
            dispatcher = testDispatcher,
            characterDetailsUseCase = fakeCharacterDetailsUseCase,
            telemetryService = fakeTelemetryService,
        )
    }

    @After
    fun teardown() {
        fakeCharacterDetailsUseCase.verifyNoFunctionsCalled()
        fakeTelemetryService.verifyNoFunctionsCalled()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        subject.state.test {
            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given FetchDetails action, when executed successfully, then emit Loading then Success state`() = runTest(testDispatcher) {
        val character = CharacterDetails(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            origin = "Earth",
            type = "Scientist",
            createdAt = "2017-11-04T18:48:46.250Z",
            imageUrl = "url",
        )
        fakeCharacterDetailsUseCase.characterDetails = character

        subject.state.test {
            subject.sendAction(CharacterDetailAction.FetchDetails("1"))
            advanceUntilIdle()

            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterDetailsUiState.Success
            val details = success.characterDetails
            assertEquals(1, details.id)
            assertEquals("Rick Sanchez", details.name)
            assertEquals("Alive", details.status)
            assertEquals("Human", details.species)
            assertEquals("Earth", details.origin)
            assertEquals("Scientist", details.type)
            assertEquals("2017-11-04T18:48:46.250Z", details.createdAt)
            assertEquals("url", details.imageUrl)

            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails("1"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given FetchDetails action with error response, when executed, then emit Loading then Error state and log telemetry`() = runTest(testDispatcher) {
        fakeCharacterDetailsUseCase.characterDetails = CharacterDetails(
            hasError = true,
            errorMsg = "Character not found",
        )

        subject.state.test {
            subject.sendAction(CharacterDetailAction.FetchDetails("999"))
            advanceUntilIdle()

            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterDetailsUiState.Error
            assertEquals("Character not found", error.message)

            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterDetailsError:UseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Character not found",
                    ),
                ),
                1,
            )
            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails("999"),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given FetchDetails action with null id, when executed, then call use case with empty string`() = runTest(testDispatcher) {
        fakeCharacterDetailsUseCase.characterDetails = CharacterDetails(id = 1)

        subject.state.test {
            subject.sendAction(CharacterDetailAction.FetchDetails(null))
            advanceUntilIdle()

            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            awaitItem() // Success

            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails(""),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given state update, when updateState called, then state changes correctly`() = runTest(testDispatcher) {
        subject.state.test {
            assertEquals(CharacterDetailsUiState.Loading, awaitItem())

            val successState = CharacterDetailsUiState.Success(CharacterUIDetails(id = 1))
            subject.updateState { successState }
            assertEquals(successState, awaitItem())

            val errorState = CharacterDetailsUiState.Error("Some error")
            subject.updateState { errorState }
            assertEquals(errorState, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ShowError event action, when sendEvent called, then event is emitted`() = runTest(testDispatcher) {
        subject.events.test {
            val errorMsg = "Direct error"
            subject.sendEvent(CharacterDetailsEvent.ShowError(errorMsg))

            val event = awaitItem() as CharacterDetailsEvent.ShowError
            assertEquals(errorMsg, event.message)

            cancelAndConsumeRemainingEvents()
        }
    }
}
