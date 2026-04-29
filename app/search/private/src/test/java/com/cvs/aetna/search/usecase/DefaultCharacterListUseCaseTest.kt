package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.fake.FakeCharacterListRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCharacterListUseCaseTest {

    private lateinit var useCase: CharacterListUseCase
    private lateinit var fakeRepository: FakeCharacterListRepository

    @Before
    fun setUp() {
        fakeRepository = FakeCharacterListRepository()
        useCase = DefaultCharacterListUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
        fakeRepository.verifyNoFunctionsCalled()
    }

    @Test
    fun getCharacterListWithoutNameReturnsAllCharacters() = runTest {
        val result = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
        assertEquals(5, result.characters!!.size)
    }

    @Test
    fun getCharacterListWithNameRickReturnsRickSanchez() = runTest {
        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Rick Sanchez", result.characters!![0].name)
        assertEquals(1, result.characters!![0].id)
    }

    @Test
    fun getCharacterListWithNameMortyReturnsMortySmith() = runTest {
        val result = useCase.getCharacterList("morty")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("morty"))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Morty Smith", result.characters!![0].name)
        assertEquals(2, result.characters!![0].id)
    }

    @Test
    fun getCharacterListWithNameSmithReturnsMultipleCharacters() = runTest {
        val result = useCase.getCharacterList("smith")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("smith"))
        assertFalse(result.hasError)
        assertEquals(4, result.characters!!.size)
        val names = result.characters!!.map { it.name }
        assertTrue(names.contains("Morty Smith"))
        assertTrue(names.contains("Summer Smith"))
        assertTrue(names.contains("Beth Smith"))
        assertTrue(names.contains("Jerry Smith"))
    }

    @Test
    fun getCharacterListCaseInsensitiveFindsCharacters() = runTest {
        val result = useCase.getCharacterList("RICK")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("RICK"))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Rick Sanchez", result.characters!![0].name)
    }

    @Test
    fun getCharacterListPartialNameMatchFindsCharacters() = runTest {
        val result = useCase.getCharacterList("sum")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("sum"))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Summer Smith", result.characters!![0].name)
    }

    @Test
    fun getCharacterListWithNonexistentNameReturnsEmptyList() = runTest {
        val result = useCase.getCharacterList("nonexistent")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("nonexistent"))
        assertFalse(result.hasError)
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun getCharacterListWithNonexistentNameHasMorePageFalse() = runTest {
        val result = useCase.getCharacterList("xyz123")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("xyz123"))
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun getCharacterListWhenRepositoryReturnsErrorPropagatesError() = runTest {
        fakeRepository.setShouldReturnError(true, "Network error")

        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertTrue(result.hasError)
        assertEquals("Network error", result.errorMsg)
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun getCharacterListWhenRepositoryReturnsErrorWithoutMessageReturnsDefaultError() = runTest {
        fakeRepository.setShouldReturnError(true)

        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertTrue(result.hasError)
        assertEquals("Fake repository error", result.errorMsg)
    }

    @Test
    fun getCharacterListAfterErrorRecoverySucceeds() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = useCase.getCharacterList("rick")
        assertTrue(errorResult.hasError)
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        fakeRepository.reset()
        val successResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters!!.size)
    }

    @Test
    fun getCharacterListRickHasCompleteData() = runTest {
        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        val rick = result.characters!![0]

        assertEquals(1, rick.id)
        assertEquals("Rick Sanchez", rick.name)
        assertEquals("Alive", rick.status)
        assertEquals("Human", rick.species)
        assertEquals("Earth (C-137)", rick.origin)
        assertNotNull(rick.imageUrl)
        assertNotNull(rick.createdAt)
    }

    @Test
    fun getCharacterListMortyHasAllRequiredFields() = runTest {
        val result = useCase.getCharacterList("morty")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("morty"))
        val morty = result.characters!![0]

        assertNotNull(morty.id)
        assertNotNull(morty.name)
        assertNotNull(morty.status)
        assertNotNull(morty.species)
        assertNotNull(morty.origin)
        assertNotNull(morty.imageUrl)
        assertNotNull(morty.createdAt)
    }

    @Test
    fun getCharacterListAllCharactersHaveValidImageUrls() = runTest {
        val result = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        result.characters!!.forEach { character ->
            assertTrue(character.imageUrl?.startsWith("https://") == true)
            assertTrue(character.imageUrl?.contains("rickandmortyapi.com") == true)
        }
    }

    @Test
    fun getCharacterListAllCharactersHasMorePage() = runTest {
        val result = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("page=2") == true)
    }

    @Test
    fun getCharacterListSingleCharacterNoMorePage() = runTest {
        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun getCharacterListFilteredResultsPaginationUrlIncludesFilter() = runTest {
        val result = useCase.getCharacterList("smith")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("smith"))
        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("smith") == true)
    }

    @Test
    fun getCharacterListMultipleCallsWithDifferentNamesReturnCorrectResults() = runTest {
        val rickResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        val mortyResult = useCase.getCharacterList("morty")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("morty"))
        val smithResult = useCase.getCharacterList("smith")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("smith"))
        assertEquals(1, rickResult.characters!!.size)
        assertEquals(1, mortyResult.characters!!.size)
        assertEquals(4, smithResult.characters!!.size)
    }

    @Test
    fun getCharacterListMultipleCallsWithSameNameReturnConsistentResults() = runTest {
        val result1 = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        val result2 = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))

        assertEquals(result1.characters!!.size, result2.characters!!.size)
        assertEquals(result1.characters!![0].name, result2.characters!![0].name)
        assertEquals(result1.characters!![0].id, result2.characters!![0].id)
    }

    @Test
    fun getCharacterListCallAllValidIdsAllSucceed() = runTest {
        val names = listOf("rick", "morty", "summer", "beth", "jerry")

        names.forEach { name ->
            val result = useCase.getCharacterList(name)
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(name))
            assertFalse(result.hasError)
            assertEquals(1, result.characters!!.size)
        }
    }

    @Test
    fun getCharacterListSmithFilterReturns4Characters() = runTest {
        val result = useCase.getCharacterList("smith")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("smith"))
        assertEquals(4, result.characters!!.size)
        val smithCharacters = result.characters!!.count { it.name?.contains("Smith") == true }
        assertEquals(4, smithCharacters)
    }

    @Test
    fun getCharacterListRickFilterReturns1Character() = runTest {
        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertEquals(1, result.characters!!.size)
        assertTrue(result.characters!![0].name?.contains("Rick") == true)
    }

    @Test
    fun getCharacterListEmptyFilterReturnAllCharacters() = runTest {
        val emptyResult = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        val allResult = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        assertEquals(emptyResult.characters!!.size, allResult.characters!!.size)
        assertEquals(5, emptyResult.characters!!.size)
    }

    @Test
    fun getCharacterListWithWhitespaceNameReturnsAllCharacters() = runTest {
        val result = useCase.getCharacterList("   ")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("   "))
        assertEquals(5, result.characters!!.size)
    }

    @Test
    fun getCharacterListWithSpecialCharactersReturnsEmpty() = runTest {
        val result = useCase.getCharacterList("!@#$%")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("!@#$%"))
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun getCharacterListWithVeryLongNameReturnsEmpty() = runTest {
        val result = useCase.getCharacterList("a".repeat(1000))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("a".repeat(1000)))
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun getCharacterListDelegatesToRepository() = runTest {
        val result = useCase.getCharacterList("morty")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("morty"))
        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
    }

    @Test
    fun getCharacterListRepositoryErrorIsPropagated() = runTest {
        fakeRepository.setShouldReturnError(true, "Test error")

        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertTrue(result.hasError)
        assertEquals("Test error", result.errorMsg)
    }

    @Test
    fun getCharacterListRepositorySuccessIsPropagated() = runTest {
        fakeRepository.reset()

        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
    }

    @Test
    fun useCaseCanFetchAnyCharacter() = runTest {
        val characters = listOf("Rick", "Morty", "Summer", "Beth", "Jerry")

        characters.forEach { characterName ->
            val result = useCase.getCharacterList(characterName)
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterName))
            assertTrue(result.characters!!.isNotEmpty())
        }
    }

    @Test
    fun useCaseHandlesSequentialCalls() = runTest {
        val names = listOf("rick", "morty", "summer", "beth", "jerry", "smith", "")

        names.forEach { name ->
            val result = useCase.getCharacterList(name)
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(name))
            assertFalse(result.hasError)
            assertNotNull(result.characters)
        }
    }

    @Test
    fun useCaseWithErrorSimulationThenRecoveryWorksCorrectly() = runTest {
        fakeRepository.setShouldReturnError(true, "Simulated error")
        val errorResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertTrue(errorResult.hasError)

        fakeRepository.reset()
        val successResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters!!.size)
    }

    @Test
    fun useCaseIsFunctionalRepositoryWrapper() = runTest {
        val useCaseResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        val repositoryResult = fakeRepository.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))

        assertEquals(useCaseResult.characters!!.size, repositoryResult.characters!!.size)
        assertEquals(useCaseResult.characters!![0].name, repositoryResult.characters!![0].name)
        assertEquals(useCaseResult.hasError, repositoryResult.hasError)
    }

    @Test
    fun useCaseMultipleInstancesWithSameRepositoryReturnConsistentResults() = runTest {
        val useCase1 = DefaultCharacterListUseCase(fakeRepository)
        val useCase2 = DefaultCharacterListUseCase(fakeRepository)

        val result1 = useCase1.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        val result2 = useCase2.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))

        assertEquals(result1.characters!!.size, result2.characters!!.size)
        assertEquals(result1.characters!![0].name, result2.characters!![0].name)
    }

    @Test
    fun useCaseResetsState() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertTrue(errorResult.hasError)

        fakeRepository.reset()
        val successResult = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        assertFalse(successResult.hasError)
    }

    @Test
    fun useCaseHandlesBoundaryIds() = runTest {
        val result = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        val ids = result.characters!!.map { it.id }

        assertEquals(listOf(1, 2, 3, 4, 5), ids)
    }

    @Test
    fun useCaseSearchResultsAreFiltered() = runTest {
        val allResult = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        val filteredResult = useCase.getCharacterList("smith")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("smith"))

        assertTrue(allResult.characters!!.size > filteredResult.characters!!.size)
        assertEquals(5, allResult.characters!!.size)
        assertEquals(4, filteredResult.characters!!.size)
    }

    @Test
    fun useCaseProvidesConsistentDataAcrossMultipleCalls() = runTest {
        val call1 = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        val call2 = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))

        assertEquals(call1.characters!!.size, call2.characters!!.size)
        call1.characters!!.forEachIndexed { index, character ->
            assertEquals(character.id, call2.characters!![index].id)
            assertEquals(character.name, call2.characters!![index].name)
        }
    }

    @Test
    fun useCaseNoFieldsAreMissingForValidSearch() = runTest {
        val result = useCase.getCharacterList("rick")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList("rick"))
        result.characters!!.forEach { character ->
            assertNotNull(character.id)
            assertNotNull(character.name)
            assertNotNull(character.status)
            assertNotNull(character.species)
            assertNotNull(character.origin)
            assertNotNull(character.imageUrl)
            assertNotNull(character.createdAt)
        }
    }

    @Test
    fun useCaseAllCharacterIdsAreUnique() = runTest {
        val result = useCase.getCharacterList("")
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(""))
        val ids = result.characters!!.map { it.id }.toSet()
        assertEquals(result.characters!!.size, ids.size)
    }

    @Test
    fun useCaseResultIsConsistentRegardlessOfSearchTermCase() = runTest {
        val searches = listOf("rick", "RICK", "Rick", "RiCk")

        searches.forEach { search ->
            val result = useCase.getCharacterList(search)
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(search))
            assertEquals(1, result.characters!!.size)
            assertEquals("Rick Sanchez", result.characters!![0].name)
        }
    }
}
