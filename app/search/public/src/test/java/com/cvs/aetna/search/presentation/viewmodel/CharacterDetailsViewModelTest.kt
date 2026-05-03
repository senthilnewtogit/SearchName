package com.cvs.aetna.search.presentation.viewmodel

import app.cash.turbine.test
import com.cvs.aetna.search.analytics.CharacterDetailsAdobeTagUseCase
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.ShareData
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
import com.cvs.aetna.search.domain.usecase.ShareCharacterUseCase
import com.cvs.aetna.search.fake.FakeCharacterDetailsAdobeTagUseCase
import com.cvs.aetna.search.fake.FakeCharacterDetailsUseCase
import com.cvs.aetna.search.fake.FakeShareCharacterUseCase
import com.cvs.aetna.search.fake.FakeTelemetryService
import com.cvs.aetna.search.fake.verifyNoMoreFakesCalled
import com.cvs.aetna.search.logger.TelemetryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailsViewModelTest {

    private lateinit var subject: CharacterDetailsViewModel
    private val fakeTelemetryService = FakeTelemetryService()
    private val fakeCharacterDetailsUseCase = FakeCharacterDetailsUseCase()
    private val testDispatcher = StandardTestDispatcher()
    private val fakeShareCharacterUseCase: FakeShareCharacterUseCase = FakeShareCharacterUseCase()

    private val fakeCharacterDetailsAdobeTagUseCase = FakeCharacterDetailsAdobeTagUseCase()

    @Before
    fun setup() {
        init()
    }

    private fun init(
        dispatcher: TestDispatcher = testDispatcher,
        characterDetailsUseCase: CharacterDetailsUseCase = fakeCharacterDetailsUseCase,
        telemetryService: TelemetryService = fakeTelemetryService,
        shareCharacterUseCase: ShareCharacterUseCase = fakeShareCharacterUseCase,
        characterDetailsAdobeTagUseCase: CharacterDetailsAdobeTagUseCase = fakeCharacterDetailsAdobeTagUseCase,
    ) {
        subject = CharacterDetailsViewModel(
            dispatcher = dispatcher,
            characterDetailsUseCase = characterDetailsUseCase,
            telemetryService = telemetryService,
            shareCharacterUseCase = shareCharacterUseCase,
            characterDetailsAdobeTagUseCase = characterDetailsAdobeTagUseCase,
        )
    }

    @After
    fun teardown() {
        listOf(
            fakeCharacterDetailsUseCase,
            fakeTelemetryService,
            fakeShareCharacterUseCase,
            fakeCharacterDetailsAdobeTagUseCase,
        ).verifyNoMoreFakesCalled()
    }

    @Test
    fun `given initial state, when ViewModel is created, then state is Loading`() = runTest(testDispatcher) {
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
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
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
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
            )
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsErrorEvent(message = "Character not found"),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given FetchDetails action with exception , when executed, then emit Loading then Error state and log telemetry`() = runTest(testDispatcher) {
        val characterDetailsUseCase = object : CharacterDetailsUseCase {
            override suspend fun getCharacterDetails(id: String): CharacterDetails = throw Exception("Null pointer exception")
        }
        init(characterDetailsUseCase = characterDetailsUseCase)

        subject.state.test {
            subject.sendAction(CharacterDetailAction.FetchDetails("999"))
            advanceUntilIdle()

            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterDetailsUiState.Error
            assertEquals("Null pointer exception", error.message)

            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterDetailsError:Handled Exception",
                    params = mapOf(
                        "errorType" to "Handled Exception",
                        "errorMessage" to "Null pointer exception",
                    ),
                ),
                1,
            )
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
            )
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsErrorEvent(message = "Null pointer exception"),
            )

            fakeCharacterDetailsUseCase.verifyFunctionNeverCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails("999"),
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
            val success = awaitItem()
            assertTrue(success is CharacterDetailsUiState.Success)

            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails(""),
                1,
            )
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Share action, when executed, then call share character use case`() = runTest(testDispatcher) {
        val character = CharacterDetails(id = 1, name = "Rick", imageUrl = "url")
        fakeCharacterDetailsUseCase.characterDetails = character

        subject.sendAction(CharacterDetailAction.FetchDetails("1"))
        advanceUntilIdle()

        subject.state.test {
            assertEquals(CharacterDetailsUiState.Loading, awaitItem())
            val success = awaitItem()
            assertTrue(success is CharacterDetailsUiState.Success)
            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails("1"),
                1,
            )
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
            )
        }
        subject.events.test {
            subject.sendAction(CharacterDetailAction.Share)
            advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is CharacterDetailsEvent.ShareImageData)
            cancelAndConsumeRemainingEvents()
        }
        fakeShareCharacterUseCase.verifyFunctionCalled(
            FakeShareCharacterUseCase.Function.ShareCharacterImage(character),
            1,
        )
        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsShareAction,
        )
    }

    @Test
    fun `given Share action when cache is empty, then emit ShowError event with NO_SHARE_DATA_FOUND`() = runTest(testDispatcher) {
        subject.events.test {
            subject.sendAction(CharacterDetailAction.Share)
            advanceUntilIdle()
            val event = awaitItem() as CharacterDetailsEvent.ShowError
            assertEquals(NO_SHARE_DATA_FOUND, event.message)
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsShareAction,
            1,
        )
    }

    @Test
    fun `given ShareAppNotFound action, when executed, then emit ShowError event with NO_APP_FOUND`() = runTest(testDispatcher) {
        subject.events.test {
            subject.sendAction(CharacterDetailAction.ShareAppNotFound)
            advanceUntilIdle()
            val event = awaitItem() as CharacterDetailsEvent.ShowError
            assertEquals(NO_APP_FOUND, event.message)
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsShareNotFoundAction,
        )
    }

    @Test
    fun `given Share action, when share use case throws exception, then emit Error state and log telemetry`() = runTest(testDispatcher) {
        val character = CharacterDetails(id = 1, name = "Rick", imageUrl = "url")
        fakeCharacterDetailsUseCase.characterDetails = character
        val shareCharacterUseCase = object : ShareCharacterUseCase {
            override suspend fun shareCharacterImage(character: CharacterDetails): ShareData = throw Exception("Share failed")
        }
        init(shareCharacterUseCase = shareCharacterUseCase)
        subject.sendAction(CharacterDetailAction.FetchDetails("1"))
        advanceUntilIdle()
        subject.state.test {
            subject.sendAction(CharacterDetailAction.Share)
            advanceUntilIdle()
            awaitItem()
            awaitItem()
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsShareAction,
            )
            fakeCharacterDetailsUseCase.verifyFunctionCalled(
                FakeCharacterDetailsUseCase.Function.GetCharacterDetails(
                    id = "1",
                ),
                1,
            )
            val error = awaitItem() as CharacterDetailsUiState.Error
            assertEquals("Share failed", error.message)
            fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
                FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsErrorEvent(message = "Share failed"),
            )
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterDetailsError:Handled Exception",
                    params = mapOf(
                        "errorType" to "Handled Exception",
                        "errorMessage" to "Share failed",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsFetchAction,
        )
    }

    @Test
    fun `given OnPageLoad action, when executed, then call Adobe tag screen load`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterDetailAction.OnPageLoad)
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsScreenLoad,
            1,
        )
    }

    @Test
    fun `given multiple OnPageLoad actions, when executed, then call Adobe tag screen load only once`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterDetailAction.OnPageLoad)
            subject.sendAction(CharacterDetailAction.OnPageLoad)
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterDetailsAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterDetailsAdobeTagUseCase.Function.OnDetailsScreenLoad,
            1,
        )
    }
}
