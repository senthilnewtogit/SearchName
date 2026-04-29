package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.fake.FakeCharacterDetailsRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCharacterDetailsUseCaseTest {

    private lateinit var useCase: DefaultCharacterDetailsUseCase
    private lateinit var fakeRepository: FakeCharacterDetailsRepository

    @Before
    fun setUp() {
        fakeRepository = FakeCharacterDetailsRepository()
        useCase = DefaultCharacterDetailsUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        fakeRepository.verifyNoFunctionsCalled()
    }

    @Test
    fun getCharacterDetails_withId1_returnsRickSanchez() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        assertNotNull(result)
        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun getCharacterDetails_withId2_returnsMortySmith() = runTest {
        val result = useCase.getCharacterDetails("2")

        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))
        assertNotNull(result)
        assertEquals(2, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertFalse(result.hasError)
    }

    @Test
    fun getCharacterDetails_withId3_returnsSummerSmith() = runTest {
        val result = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertFalse(result.hasError)
    }

    @Test
    fun getCharacterDetails_withId4_returnsBethSmith() = runTest {
        val result = useCase.getCharacterDetails("4")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("4"))

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
        assertFalse(result.hasError)
    }

    @Test
    fun getCharacterDetails_withId5_returnsJerrySmith() = runTest {
        val result = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
        assertFalse(result.hasError)
    }

    @Test
    fun getCharacterDetails_returnsAllRequiredFields() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.id)
        assertNotNull(result.name)
        assertNotNull(result.status)
        assertNotNull(result.species)
        assertNotNull(result.origin)
        assertNotNull(result.imageUrl)
        assertNotNull(result.createdAt)
    }

    @Test
    fun getCharacterDetails_returnsCorrectOrigin() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun getCharacterDetails_returnsValidImageUrl() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.imageUrl?.startsWith("https://") == true)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun getCharacterDetails_returnsValidCreatedDate() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("2017") == true)
    }

    @Test
    fun getCharacterDetails_noErrorForValidId() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    // ==================== Invalid IDs ====================

    @Test
    fun getCharacterDetails_withInvalidId999_returnsError() = runTest {
        val result = useCase.getCharacterDetails("999")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("999"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun getCharacterDetails_withNonNumericId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("abc")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("abc"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun getCharacterDetails_withNegativeId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("-1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("-1"))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_withEmptyId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(""))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_withSpecialCharacterId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("@#$")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("@#$"))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_withZeroId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("0")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("0"))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_whenRepositoryReturnsError_propagatesError() = runTest {
        fakeRepository.setShouldReturnError(true, "Repository Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Repository Error", result.errorMsg)
    }

    @Test
    fun getCharacterDetails_whenRepositoryReturnsError_noData() = runTest {
        fakeRepository.setShouldReturnError(true)

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertNull(result.name)
    }

    @Test
    fun getCharacterDetails_afterErrorRecovery_succeeds() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        // Reset
        fakeRepository.setShouldReturnError(false)

        val successResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
        assertEquals("Rick Sanchez", successResult.name)
    }

    // ==================== Multiple Calls ====================

    @Test
    fun getCharacterDetails_multipleCallsWithDifferentIds_returnCorrectResults() = runTest {
        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))
        val result3 = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))

        assertEquals("Rick Sanchez", result1.name)
        assertEquals("Morty Smith", result2.name)
        assertEquals("Summer Smith", result3.name)

        assertFalse(result1.hasError)
        assertFalse(result2.hasError)
        assertFalse(result3.hasError)
    }

    @Test
    fun getCharacterDetails_multipleCallsWithSameId_returnConsistentResults() = runTest {
        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals(result1.id, result2.id)
        assertEquals(result1.name, result2.name)
        assertEquals(result1.status, result2.status)
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun getCharacterDetails_callAllValidIds_allSucceed() = runTest {
        val results = (1..5).map {
            useCase.getCharacterDetails(it.toString())
        }
        for (i in 1..5) {
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(i.toString()))
        }

        results.forEach { result ->
            assertFalse(result.hasError)
            assertNotNull(result.name)
            assertNotNull(result.id)
        }
    }

    @Test
    fun getCharacterDetails_rickSanchez_hasCorrectAllData() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun getCharacterDetails_mortySmith_hasCorrectAllData() = runTest {
        val result = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))

        assertEquals(2, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
    }

    @Test
    fun getCharacterDetails_summerSmith_isCorrect() = runTest {
        val result = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
    }

    @Test
    fun getCharacterDetails_bethSmith_isCorrect() = runTest {
        val result = useCase.getCharacterDetails("4")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("4"))

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun getCharacterDetails_jerrySmith_isCorrect() = runTest {
        val result = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
    }

    @Test
    fun getCharacterDetails_idTypeIsCorrect() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.id is Int)
        assertEquals(1, result.id)
    }

    @Test
    fun getCharacterDetails_nameIsNotEmpty() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.name)
        assertTrue(result.name?.isNotEmpty() == true)
    }

    @Test
    fun getCharacterDetails_statusIsAlive() = runTest {
        (1..5).forEach { id ->
            val result = useCase.getCharacterDetails(id.toString())
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(id.toString()))
            assertEquals("Alive", result.status)
        }
    }

    @Test
    fun getCharacterDetails_speciesIsHuman() = runTest {
        (1..5).forEach { id ->
            val result = useCase.getCharacterDetails(id.toString())
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(id.toString()))
            assertEquals("Human", result.species)
        }
    }

    @Test
    fun getCharacterDetails_imageUrlIsValid() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.imageUrl)
        assertTrue(result.imageUrl?.startsWith("https://") == true)
    }

    @Test
    fun getCharacterDetails_withWhitespaceId_isHandledAsInvalid() = runTest {
        val result = useCase.getCharacterDetails("  1  ")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("  1  "))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_withVeryLargeId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("99999999999")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("99999999999"))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_withFloatId_returnsError() = runTest {
        val result = useCase.getCharacterDetails("1.5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1.5"))

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterDetails_delegatesToRepository() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        assertFalse(result.hasError)
        assertEquals("Rick Sanchez", result.name)
    }

    @Test
    fun getCharacterDetails_repositoryErrorIsPropagated() = runTest {
        fakeRepository.setShouldReturnError(true, "Test Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Test Error", result.errorMsg)
    }

    @Test
    fun getCharacterDetails_useCaseCanFetchAnyCharacter() = runTest {
        val rick = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val morty = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))
        val summer = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))
        val beth = useCase.getCharacterDetails("4")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("4"))
        val jerry = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))

        assertEquals("Rick Sanchez", rick.name)
        assertEquals("Morty Smith", morty.name)
        assertEquals("Summer Smith", summer.name)
        assertEquals("Beth Smith", beth.name)
        assertEquals("Jerry Smith", jerry.name)
    }

    @Test
    fun getCharacterDetails_useCaseHandlesSequentialCalls() = runTest {
        val characters = mutableListOf<String>()
        for (i in 1..5) {
            val result = useCase.getCharacterDetails(i.toString())
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(i.toString()))
            characters.add(result.name ?: "Unknown")
        }

        assertEquals(5, characters.size)
        assertTrue(characters.contains("Rick Sanchez"))
        assertTrue(characters.contains("Morty Smith"))
    }

    @Test
    fun getCharacterDetails_useCaseWithErrorSimulation() = runTest {
        fakeRepository.setShouldReturnError(true, "Network Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Network Error", result.errorMsg)

        // Reset and verify recovery
        fakeRepository.setShouldReturnError(false)
        val recoveredResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        assertFalse(recoveredResult.hasError)
    }

    @Test
    fun getCharacterDetails_useCaseIsFunctionalRepositoryWrapper() = runTest {
        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))

        assertEquals("Rick Sanchez", result1.name)
        assertEquals("Morty Smith", result2.name)
    }

    @Test
    fun getCharacterDetails_multipleUseCaseInstancesWithSameRepository() = runTest {
        val useCase2 = DefaultCharacterDetailsUseCase(fakeRepository)

        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase2.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals(result1.name, result2.name)
        assertEquals(result1.id, result2.id)
    }

    @Test
    fun getCharacterDetails_useCaseResetsState() = runTest {
        fakeRepository.setShouldReturnError(true)

        val errorResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        fakeRepository.reset()
        val successResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
    }

    @Test
    fun getCharacterDetails_handlesBoundaryIds() = runTest {
        val validMin = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val validMax = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))
        val invalidBefore = useCase.getCharacterDetails("0")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("0"))
        val invalidAfter = useCase.getCharacterDetails("6")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("6"))

        assertFalse(validMin.hasError)
        assertFalse(validMax.hasError)
        assertTrue(invalidBefore.hasError)
        assertTrue(invalidAfter.hasError)
    }
}
