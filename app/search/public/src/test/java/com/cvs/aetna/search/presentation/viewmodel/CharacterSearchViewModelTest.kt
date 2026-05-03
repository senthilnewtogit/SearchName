package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.fake.FakeCharacterListAdobeTagUseCase
import com.cvs.aetna.search.fake.FakeCharacterListUseCase
import com.cvs.aetna.search.fake.FakeTelemetryService
import com.cvs.aetna.search.fake.verifyNoMoreFakesCalled
import com.cvs.aetna.search.logger.TelemetryService
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.model.UiText
import com.cvs.aetna.search.pub.R
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
class CharacterSearchViewModelTest {

    private lateinit var subject: CharacterSearchViewModel
    private val fakeSavedStateHandle: SavedStateHandle = SavedStateHandle()
    private val fakeTelemetryService = FakeTelemetryService()
    private val fakeCharacterListUseCase = FakeCharacterListUseCase()
    private val testDispatcher = StandardTestDispatcher()

    private val fakeCharacterListAdobeTagUseCase = FakeCharacterListAdobeTagUseCase()

    @Before
    fun setup() {
        init()
    }

    private fun init(
        telemetryService: TelemetryService = fakeTelemetryService,
        characterListUseCase: CharacterListUseCase = fakeCharacterListUseCase,
        dispatcher: TestDispatcher = testDispatcher,
    ) {
        subject = CharacterSearchViewModel(
            savedStateHandle = fakeSavedStateHandle,
            telemetryService = telemetryService,
            characterListUseCase = characterListUseCase,
            dispatcher = dispatcher,
            adobeTagUseCase = fakeCharacterListAdobeTagUseCase,
        )
    }

    @After
    fun teardown() {
        listOf(
            fakeCharacterListUseCase,
            fakeTelemetryService,
            fakeCharacterListAdobeTagUseCase,
        ).verifyNoMoreFakesCalled()
    }

    @Test
    fun `given initial state, when ViewModel is created, then state is Empty`() = runTest(testDispatcher) {
        subject.state.test {
            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given initial state, when ViewModel is created, then it has no events`() = runTest(testDispatcher) {
        subject.events.test {
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            assertEquals(1, success.charactersList.size)
            assertEquals(1, success.charactersList[0].id)
            assertEquals("Rick Sanchez", success.charactersList[0].name)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = ""))
            assertEquals(3, success.charactersList.size)

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "nonexistent"))
            assertTrue(success.charactersList.isEmpty())

            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "nonexistent",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given multiple Search actions, when executed sequentially, then emit correct states each time`() = runTest(testDispatcher) {
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "smith"))
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "smith",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with error response, when executed, then emit Loading then Error state`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            errorMsg = CharacterError.NetworkIO("Network error"),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            val uiText = error.message as UiText.StringResource
            assertEquals(R.string.error_network, uiText.resId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent(message = "NetworkIO(message=Network error)"))
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "NetworkIO(message=Network error)",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Search action with null error message, when executed, then emit Success with null message`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            errorMsg = null,
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            awaitItem() as CharacterSearchUiState.Success

            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            fakeCharacterListAdobeTagUseCase.verifyFunctionNeverCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent(message = "Unknown error"))
            fakeTelemetryService.verifyFunctionNeverCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Unknown error",
                    ),
                ),

            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnClickImage(characterId = "123"))
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnClickImage(characterId = "1"))
            val event2 = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("2", event2.characterId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnClickImage(characterId = "2"))
            val event3 = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("3", event3.characterId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnClickImage(characterId = "3"))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ReachedEndOfList action, when executed, then no state changes occur`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()

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
        val fakeCharacterListUseCase = object : CharacterListUseCase {
            override suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList = throw NullPointerException("Null Pointer Exception")

            override suspend fun getMoreCharacterList(url: String): CharacterList = throw NullPointerException("Null Pointer Exception")
        }
        init(characterListUseCase = fakeCharacterListUseCase)

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            val uiText = error.message as UiText.StringResource
            assertEquals(R.string.error_unknown, uiText.resId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent("Unknown(message=Null Pointer Exception)"))
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorHandled Exception",
                    params = mapOf(
                        "errorType" to "Handled Exception",
                        "errorMessage" to "Unknown(message=Null Pointer Exception)",
                    ),
                ),
                1,
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given use case return handled exception, when search executed, then emit Error state and log telemetry`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = emptyList(),
            errorMsg = CharacterError.Unknown("Use case error"),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"), 1)
            val error = awaitItem() as CharacterSearchUiState.Error
            val uiText = error.message as UiText.StringResource
            assertEquals(R.string.error_unknown, uiText.resId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent(message = "Unknown(message=Use case error)"), 1)
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "Unknown(message=Use case error)",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = ""))
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "!@#$%"), 1)
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "!@#$%",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given complete search flow, when executed, then state transitions are correct`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        subject.state.test {
            assertEquals(CharacterSearchUiState.Empty, awaitItem())

            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success1 = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success1.charactersList.size)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            fakeCharacterListUseCase.characterList = CharacterList(
                characters = emptyList(),
                errorMsg = CharacterError.UnknownHost("Error occurred"),
            )
            subject.sendAction(CharacterSearchAction.Search("error"))
            advanceUntilIdle()
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val error = awaitItem() as CharacterSearchUiState.Error
            val uiText = error.message as UiText.StringResource
            assertEquals(R.string.error_no_internet, uiText.resId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "error"))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent(message = "UnknownHost(message=Error occurred)"))
            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "UnknownHost(message=Error occurred)",
                    ),
                ),
                1,
            )

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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "smith"))
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "rick",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "error",
                    ),
                ),
                1,
            )
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "smith",
                    ),
                ),
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            cancelAndConsumeRemainingEvents()
        }

        subject.events.test {
            subject.sendAction(CharacterSearchAction.OnCharacterClick("1"))
            advanceUntilIdle()

            val event = awaitItem() as CharacterSearchEvent.NavigateToDetails
            assertEquals("1", event.characterId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnClickImage(characterId = "1"))
            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetCharacterList(
                characterSearch = CharacterSearch(
                    name = "rick",
                ),
            ),
            1,
        )
    }

    @Test
    fun `given rapid successive actions, when executed, then handle gracefully`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            subject.sendAction(CharacterSearchAction.Search("morty"))
            subject.sendAction(CharacterSearchAction.Search("summer"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"), 1)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "morty"), 1)
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "summer",
                    ),
                ),
                3,
            )
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "summer"), 1)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ViewModel recreated, when actions sent, then handle correctly`() = runTest(testDispatcher) {
        val newViewModel = CharacterSearchViewModel(
            savedStateHandle = SavedStateHandle(),
            telemetryService = FakeTelemetryService(),
            characterListUseCase = fakeCharacterListUseCase,
            dispatcher = testDispatcher,
            adobeTagUseCase = fakeCharacterListAdobeTagUseCase,
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
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"), 1)
            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetCharacterList(
                characterSearch = CharacterSearch(
                    name = "rick",
                ),
            ),
            1,
        )
    }

    @Test
    fun `given OnFilterUpdate action with species Human, when executed, then emit Loading then Success state`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(
                CharacterDetails(
                    id = 1,
                    name = "Rick Sanchez",
                    species = "Human",
                    imageUrl = "url1",
                ),
            ),
        )

        subject.state.test {
            val filter = CharacterFilterUiState(species = "Human")
            subject.sendAction(CharacterSearchAction.OnFilterUpdate(filter))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnFilterApplyAction)
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        species = "Human",
                        name = "",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given OnResetFilter action, when executed, then reset filter and fetch character list`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            listOf(CharacterDetails(id = 1, name = "Rick", imageUrl = "url")),
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.OnResetFilter)
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success.charactersList.size)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnFilterResetAction)
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(
                        name = "",
                        status = null,
                        species = null,
                        type = null,
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given Success state with more pages, when ReachedEndOfList action executed, then load and append more characters`() = runTest(testDispatcher) {
        val initialCharacter = CharacterDetails(id = 1, name = "Rick")
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = listOf(initialCharacter),
            totalCount = 2,
            nextPageUrl = "https://page2.com",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()

            assertEquals(CharacterSearchUiState.Empty, awaitItem())
            assertEquals(CharacterSearchUiState.Loading, awaitItem())
            val success1 = awaitItem() as CharacterSearchUiState.Success
            assertEquals(1, success1.charactersList.size)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            assertTrue(success1.hasNextPage)
            fakeCharacterListUseCase.verifyFunctionCalled(
                FakeCharacterListUseCase.Function.GetCharacterList(
                    characterSearch = CharacterSearch(name = "rick"),
                ),
            )
            val moreCharacter = CharacterDetails(id = 2, name = "Morty")
            fakeCharacterListUseCase.moreCharacterList = CharacterList(
                characters = listOf(moreCharacter),
                totalCount = 2,
                nextPageUrl = null,
            )

            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()

            val loadingMoreState = awaitItem() as CharacterSearchUiState.Success
            assertTrue(loadingMoreState.isLoadingMore)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageLoadMoreAction)
            val success2 = awaitItem() as CharacterSearchUiState.Success
            assertEquals(2, success2.charactersList.size)
            assertEquals("Rick", success2.charactersList[0].name)
            assertEquals("Morty", success2.charactersList[1].name)
            assertEquals(false, success2.hasNextPage)
            assertEquals(false, success2.isLoadingMore)

            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetMoreCharacterList("https://page2.com"),
            1,
        )
    }

    @Test
    fun `given ReachedEndOfList action when all items are loaded then no additional items are fetched`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = listOf(CharacterDetails(id = 1, name = "Rick")),
            totalCount = 1,
            nextPageUrl = "https://page2.com",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            awaitItem() // Empty
            awaitItem() // Loading
            assertEquals(1, (awaitItem() as CharacterSearchUiState.Success).charactersList.size)
            fakeCharacterListUseCase.verifyFunctionCalled(FakeCharacterListUseCase.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()
            fakeCharacterListAdobeTagUseCase.verifyFunctionNeverCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageLoadMoreAction)
            expectNoEvents()
        }

        fakeCharacterListUseCase.verifyFunctionNeverCalled(
            FakeCharacterListUseCase.Function.GetMoreCharacterList("https://page2.com"),
        )
    }

    @Test
    fun `given fetchMoreCharacterList returns error then emit Error state and log telemetry`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = listOf(CharacterDetails(id = 1, name = "Rick")),
            totalCount = 2,
            nextPageUrl = "https://page2.com",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            awaitItem() // Empty
            awaitItem() // Loading
            awaitItem() // Success
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            fakeCharacterListUseCase.verifyFunctionCalled(FakeCharacterListUseCase.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
            fakeCharacterListUseCase.moreCharacterList = CharacterList(
                characters = emptyList(),
                errorMsg = CharacterError.NetworkIO("Pagination error"),
            )

            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()
            awaitItem()
            val error = awaitItem() as CharacterSearchUiState.Error
            val uiText = error.message as UiText.StringResource
            assertEquals(R.string.error_network, uiText.resId)
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageLoadMoreAction)
            fakeCharacterListUseCase.verifyFunctionCalled(FakeCharacterListUseCase.Function.GetMoreCharacterList(url = "https://page2.com"))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageErrorEvent("NetworkIO(message=Pagination error)"))

            fakeTelemetryService.verifyFunctionCalled(
                FakeTelemetryService.Function.LogEvent(
                    eventName = "CharacterSearchErrorUseCase Exception",
                    params = mapOf(
                        "errorType" to "UseCase Exception",
                        "errorMessage" to "NetworkIO(message=Pagination error)",
                    ),
                ),
                1,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given ReachedEndOfList action when nextLoadUrl is null then no additional items are fetched`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = listOf(CharacterDetails(id = 1, name = "Rick")),
            totalCount = 5,
            nextPageUrl = null,
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            awaitItem() // Empty
            awaitItem() // Loading
            awaitItem() // Success
            fakeCharacterListUseCase.verifyFunctionCalled(FakeCharacterListUseCase.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            advanceUntilIdle()
            fakeCharacterListAdobeTagUseCase.verifyFunctionNeverCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageLoadMoreAction)
            expectNoEvents()
        }

        fakeCharacterListUseCase.verifyFunctionNeverCalled(
            FakeCharacterListUseCase.Function.GetMoreCharacterList(""),
        )
    }

    @Test
    fun `given ReachedEndOfList action when already loading more then no duplicate request is sent`() = runTest(testDispatcher) {
        fakeCharacterListUseCase.characterList = CharacterList(
            characters = listOf(CharacterDetails(id = 1, name = "Rick")),
            totalCount = 5,
            nextPageUrl = "https://page2.com",
        )

        subject.state.test {
            subject.sendAction(CharacterSearchAction.Search("rick"))
            advanceUntilIdle()
            awaitItem() // Empty
            awaitItem() // Loading
            awaitItem() // Success
            fakeCharacterListUseCase.verifyFunctionCalled(FakeCharacterListUseCase.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnSearchAction(characterName = "rick"))
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)
            subject.sendAction(CharacterSearchAction.ReachedEndOfList)

            advanceUntilIdle()

            awaitItem()
            awaitItem()
            fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(FakeCharacterListAdobeTagUseCase.Function.OnPageLoadMoreAction, 1)
            cancelAndConsumeRemainingEvents()
        }

        fakeCharacterListUseCase.verifyFunctionCalled(
            FakeCharacterListUseCase.Function.GetMoreCharacterList("https://page2.com"),
            1,
        )
    }

    @Test
    fun `given OnPageLoad action, when executed, then call Adobe tag search screen load event`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterSearchAction.OnPageLoad)
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterListAdobeTagUseCase.Function.OnSearchScreenLoadEvent,
            1,
        )
    }

    @Test
    fun `given multiple OnPageLoad actions, when executed, then call Adobe tag search screen load event only once`() = runTest(testDispatcher) {
        subject.state.test {
            subject.sendAction(CharacterSearchAction.OnPageLoad)
            subject.sendAction(CharacterSearchAction.OnPageLoad)
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }
        fakeCharacterListAdobeTagUseCase.verifyFunctionCalled(
            FakeCharacterListAdobeTagUseCase.Function.OnSearchScreenLoadEvent,
            1,
        )
    }
}
