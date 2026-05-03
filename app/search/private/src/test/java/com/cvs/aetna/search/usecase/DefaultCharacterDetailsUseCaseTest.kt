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
    fun `given id 1, when getCharacterDetails called, then return Rick Sanchez`() = runTest {
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
    fun `given id 2, when getCharacterDetails called, then return Morty Smith`() = runTest {
        val result = useCase.getCharacterDetails("2")

        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))
        assertNotNull(result)
        assertEquals(2, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertFalse(result.hasError)
    }

    @Test
    fun `given id 3, when getCharacterDetails called, then return Summer Smith`() = runTest {
        val result = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertFalse(result.hasError)
    }

    @Test
    fun `given id 4, when getCharacterDetails called, then return Beth Smith`() = runTest {
        val result = useCase.getCharacterDetails("4")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("4"))

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
        assertFalse(result.hasError)
    }

    @Test
    fun `given id 5, when getCharacterDetails called, then return Jerry Smith`() = runTest {
        val result = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
        assertFalse(result.hasError)
    }

    @Test
    fun `when getCharacterDetails called, then return all required fields`() = runTest {
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
    fun `when getCharacterDetails called, then return correct origin`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun `when getCharacterDetails called, then return valid image url`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.imageUrl?.startsWith("https://") == true)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun `when getCharacterDetails called, then return valid created date`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("2017") == true)
    }

    @Test
    fun `given valid id, when getCharacterDetails called, then no error is returned`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given invalid id 999, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("999")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("999"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given non-numeric id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("abc")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("abc"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given negative id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("-1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("-1"))

        assertTrue(result.hasError)
    }

    @Test
    fun `given empty id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(""))

        assertTrue(result.hasError)
    }

    @Test
    fun `given special character id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("@#$")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("@#$"))

        assertTrue(result.hasError)
    }

    @Test
    fun `given zero id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("0")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("0"))

        assertTrue(result.hasError)
    }

    @Test
    fun `when repository returns error, then getCharacterDetails propagates error`() = runTest {
        fakeRepository.setShouldReturnError(true, "Repository Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Repository Error", result.errorMsg)
    }

    @Test
    fun `when repository returns error, then getCharacterDetails returns no data`() = runTest {
        fakeRepository.setShouldReturnError(true)

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertNull(result.name)
    }

    @Test
    fun `given error recovery, when getCharacterDetails called, then succeeds`() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        fakeRepository.setShouldReturnError(false)

        val successResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
        assertEquals("Rick Sanchez", successResult.name)
    }

    @Test
    fun `given multiple calls with different ids, then return correct results`() = runTest {
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
    fun `given multiple calls with same id, then return consistent results`() = runTest {
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
    fun `when all valid ids called, then all succeed`() = runTest {
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
    fun `given Rick Sanchez id, when getCharacterDetails called, then has correct data`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun `given Morty Smith id, when getCharacterDetails called, then has correct data`() = runTest {
        val result = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))

        assertEquals(2, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
    }

    @Test
    fun `given Summer Smith id, when getCharacterDetails called, then is correct`() = runTest {
        val result = useCase.getCharacterDetails("3")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("3"))

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
    }

    @Test
    fun `given Beth Smith id, when getCharacterDetails called, then is correct`() = runTest {
        val result = useCase.getCharacterDetails("4")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("4"))

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun `given Jerry Smith id, when getCharacterDetails called, then is correct`() = runTest {
        val result = useCase.getCharacterDetails("5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("5"))

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
    }

    @Test
    fun `when getCharacterDetails called, then id type is correct`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.id is Int)
        assertEquals(1, result.id)
    }

    @Test
    fun `when getCharacterDetails called, then name is not empty`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.name)
        assertTrue(result.name?.isNotEmpty() == true)
    }

    @Test
    fun `when getCharacterDetails called for any character, then status is Alive`() = runTest {
        (1..5).forEach { id ->
            val result = useCase.getCharacterDetails(id.toString())
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(id.toString()))
            assertEquals("Alive", result.status)
        }
    }

    @Test
    fun `when getCharacterDetails called for any character, then species is Human`() = runTest {
        (1..5).forEach { id ->
            val result = useCase.getCharacterDetails(id.toString())
            fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails(id.toString()))
            assertEquals("Human", result.species)
        }
    }

    @Test
    fun `when getCharacterDetails called, then image url is valid`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertNotNull(result.imageUrl)
        assertTrue(result.imageUrl?.startsWith("https://") == true)
    }

    @Test
    fun `given whitespace id, when getCharacterDetails called, then is handled as invalid`() = runTest {
        val result = useCase.getCharacterDetails("  1  ")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("  1  "))

        assertTrue(result.hasError)
    }

    @Test
    fun `given very large id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("99999999999")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("99999999999"))

        assertTrue(result.hasError)
    }

    @Test
    fun `given float id, when getCharacterDetails called, then return error`() = runTest {
        val result = useCase.getCharacterDetails("1.5")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1.5"))

        assertTrue(result.hasError)
    }

    @Test
    fun `when getCharacterDetails called, then delegate to repository`() = runTest {
        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        assertFalse(result.hasError)
        assertEquals("Rick Sanchez", result.name)
    }

    @Test
    fun `when repository error occurs, then getCharacterDetails propagates error`() = runTest {
        fakeRepository.setShouldReturnError(true, "Test Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Test Error", result.errorMsg)
    }

    @Test
    fun `usecase can fetch any character successfully`() = runTest {
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
    fun `usecase handles sequential calls correctly`() = runTest {
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
    fun `usecase recovery after error works correctly`() = runTest {
        fakeRepository.setShouldReturnError(true, "Network Error")

        val result = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertTrue(result.hasError)
        assertEquals("Network Error", result.errorMsg)

        fakeRepository.setShouldReturnError(false)
        val recoveredResult = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        assertFalse(recoveredResult.hasError)
    }

    @Test
    fun `usecase is functional repository wrapper`() = runTest {
        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase.getCharacterDetails("2")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("2"))

        assertEquals("Rick Sanchez", result1.name)
        assertEquals("Morty Smith", result2.name)
    }

    @Test
    fun `multiple usecase instances with same repository work correctly`() = runTest {
        val useCase2 = DefaultCharacterDetailsUseCase(fakeRepository)

        val result1 = useCase.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))
        val result2 = useCase2.getCharacterDetails("1")
        fakeRepository.verifyFunctionCalled(FakeCharacterDetailsRepository.Function.GetCharacterDetails("1"))

        assertEquals(result1.name, result2.name)
        assertEquals(result1.id, result2.id)
    }

    @Test
    fun `usecase resets state correctly`() = runTest {
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
    fun `usecase handles boundary ids correctly`() = runTest {
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
