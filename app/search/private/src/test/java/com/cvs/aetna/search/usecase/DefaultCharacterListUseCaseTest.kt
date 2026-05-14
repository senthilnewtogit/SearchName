package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterSearch
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

    private lateinit var subject: CharacterListUseCase
    private lateinit var fakeRepository: FakeCharacterListRepository

    @Before
    fun setUp() {
        fakeRepository = FakeCharacterListRepository()
        subject = DefaultCharacterListUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
        fakeRepository.verifyNoFunctionsCalled()
    }

    @Test
    fun `given empty search name when getCharacterList is called then it returns all characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
        assertEquals(5, result.characters!!.size)
    }

    @Test
    fun `given name Rick when getCharacterList is called then it returns Rick Sanchez`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Rick Sanchez", result.characters!![0].name)
        assertEquals(1, result.characters!![0].id)
    }

    @Test
    fun `given name Morty when getCharacterList is called then it returns Morty Smith`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "morty"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "morty")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Morty Smith", result.characters!![0].name)
        assertEquals(2, result.characters!![0].id)
    }

    @Test
    fun `given name Smith when getCharacterList is called then it returns multiple characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "smith"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "smith")))
        assertFalse(result.hasError)
        assertEquals(4, result.characters!!.size)
        val names = result.characters!!.map { it.name }
        assertTrue(names.contains("Morty Smith"))
        assertTrue(names.contains("Summer Smith"))
        assertTrue(names.contains("Beth Smith"))
        assertTrue(names.contains("Jerry Smith"))
    }

    @Test
    fun `given uppercase name RICK when getCharacterList is called then it finds characters case-insensitively`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "RICK"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "RICK")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Rick Sanchez", result.characters!![0].name)
    }

    @Test
    fun `given partial name sum when getCharacterList is called then it finds Summer Smith`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "sum"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "sum")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Summer Smith", result.characters!![0].name)
    }

    @Test
    fun `given nonexistent name when getCharacterList is called then it returns empty list`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "nonexistent"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "nonexistent")))
        assertFalse(result.hasError)
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun `given nonexistent name when getCharacterList is called then hasMorePage is false`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "xyz123"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "xyz123")))
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun `given repository error when getCharacterList is called then it propagates error`() = runTest {
        fakeRepository.setShouldReturnError(true, CharacterError.NoInternet("Network error"))

        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertTrue(result.hasError)
        assertEquals(CharacterError.NoInternet("Network error"), result.errorMsg)
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun `given repository error without message when getCharacterList is called then it returns default error`() = runTest {
        fakeRepository.setShouldReturnError(true)

        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertTrue(result.hasError)
        assertEquals(CharacterError.Unknown("Fake repository error"), result.errorMsg)
    }

    @Test
    fun `given error recovery when getCharacterList is called then it succeeds after initial failure`() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        assertTrue(errorResult.hasError)
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        fakeRepository.reset()
        val successResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters!!.size)
    }

    @Test
    fun `given Rick search when getCharacterList is called then characters have complete data`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
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
    fun `given Morty search when getCharacterList is called then characters have all required fields`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "morty"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "morty")))
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
    fun `given successful search when getCharacterList is called then all characters have valid image URLs`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        result.characters!!.forEach { character ->
            assertTrue(character.imageUrl?.startsWith("https://") == true)
            assertTrue(character.imageUrl?.contains("rickandmortyapi.com") == true)
        }
    }

    @Test
    fun `given empty search name when getCharacterList is called then hasMorePage is true`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("page=2") == true)
    }

    @Test
    fun `given single character result when getCharacterList is called then hasMorePage is false`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun `given filtered results when getCharacterList is called then pagination URL includes filter`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "smith"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "smith")))
        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("smith") == true)
    }

    @Test
    fun `given multiple different searches when getCharacterList is called then it returns correct results for each`() = runTest {
        val rickResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        val mortyResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "morty"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "morty")))
        val smithResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "smith"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "smith")))
        assertEquals(1, rickResult.characters!!.size)
        assertEquals(1, mortyResult.characters!!.size)
        assertEquals(4, smithResult.characters!!.size)
    }

    @Test
    fun `given same search term multiple times when getCharacterList is called then it returns consistent results`() = runTest {
        val result1 = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        val result2 = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))

        assertEquals(result1.characters!!.size, result2.characters!!.size)
        assertEquals(result1.characters!![0].name, result2.characters!![0].name)
        assertEquals(result1.characters!![0].id, result2.characters!![0].id)
    }

    @Test
    fun `given all valid names when getCharacterList is called then all searches succeed`() = runTest {
        val names = listOf("rick", "morty", "summer", "beth", "jerry")

        names.forEach { name ->
            val result = subject.getCharacterList(characterSearch = CharacterSearch(name = name))
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = name)))
            assertFalse(result.hasError)
            assertEquals(1, result.characters!!.size)
        }
    }

    @Test
    fun `given smith filter when getCharacterList is called then it returns 4 characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "smith"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "smith")))
        assertEquals(4, result.characters!!.size)
        val smithCharacters = result.characters!!.count { it.name?.contains("Smith") == true }
        assertEquals(4, smithCharacters)
    }

    @Test
    fun `given rick filter when getCharacterList is called then it returns 1 character`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertEquals(1, result.characters!!.size)
        assertTrue(result.characters!![0].name?.contains("Rick") == true)
    }

    @Test
    fun `given empty filter when getCharacterList is called then it returns all characters`() = runTest {
        val emptyResult = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        val allResult = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        assertEquals(emptyResult.characters!!.size, allResult.characters!!.size)
        assertEquals(5, emptyResult.characters!!.size)
    }

    @Test
    fun `given whitespace name when getCharacterList is called then it returns all characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "   "))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "   ")))
        assertEquals(5, result.characters!!.size)
    }

    @Test
    fun `given special characters when getCharacterList is called then it returns empty list`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "!@#$%"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "!@#$%")))
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun `given very long name when getCharacterList is called then it returns empty list`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "a".repeat(1000)))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "a".repeat(1000))))
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun `when getCharacterList is called then it delegates to repository`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "morty"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "morty")))
        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
    }

    @Test
    fun `given repository error when getCharacterList is called then error is propagated`() = runTest {
        fakeRepository.setShouldReturnError(true, CharacterError.Unknown("Test error"))

        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertTrue(result.hasError)
        assertEquals(CharacterError.Unknown("Test error"), result.errorMsg)
    }

    @Test
    fun `given repository success when getCharacterList is called then success is propagated`() = runTest {
        fakeRepository.reset()

        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
    }

    @Test
    fun `when useCase fetches any character then it returns non-empty results`() = runTest {
        val characters = listOf("Rick", "Morty", "Summer", "Beth", "Jerry")

        characters.forEach { characterName ->
            val result = subject.getCharacterList(characterSearch = CharacterSearch(name = characterName))
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = characterName)))
            assertTrue(result.characters!!.isNotEmpty())
        }
    }

    @Test
    fun `when useCase handles sequential calls then all results are valid`() = runTest {
        val names = listOf("rick", "morty", "summer", "beth", "jerry", "smith", "")

        names.forEach { name ->
            val result = subject.getCharacterList(characterSearch = CharacterSearch(name = name))
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = name)))
            assertFalse(result.hasError)
            assertNotNull(result.characters)
        }
    }

    @Test
    fun `given error simulation and then recovery when getCharacterList is called then it works correctly`() = runTest {
        fakeRepository.setShouldReturnError(true, CharacterError.Unknown("Simulated error"))
        val errorResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertTrue(errorResult.hasError)

        fakeRepository.reset()
        val successResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters!!.size)
    }

    @Test
    fun `when useCase is used as functional repository wrapper then results match repository`() = runTest {
        val useCaseResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        val repositoryResult = fakeRepository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))

        assertEquals(useCaseResult.characters!!.size, repositoryResult.characters!!.size)
        assertEquals(useCaseResult.characters!![0].name, repositoryResult.characters!![0].name)
        assertEquals(useCaseResult.hasError, repositoryResult.hasError)
    }

    @Test
    fun `given multiple useCase instances with same repository when called then they return consistent results`() = runTest {
        val useCase1 = DefaultCharacterListUseCase(fakeRepository)
        val useCase2 = DefaultCharacterListUseCase(fakeRepository)

        val result1 = useCase1.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        val result2 = useCase2.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))

        assertEquals(result1.characters!!.size, result2.characters!!.size)
        assertEquals(result1.characters!![0].name, result2.characters!![0].name)
    }

    @Test
    fun `given initial error state when repository is reset then getCharacterList succeeds`() = runTest {
        fakeRepository.setShouldReturnError(true)
        val errorResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertTrue(errorResult.hasError)

        fakeRepository.reset()
        val successResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
        assertFalse(successResult.hasError)
    }

    @Test
    fun `when all characters are fetched then they return correct boundary IDs`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        val ids = result.characters!!.map { it.id }

        assertEquals(listOf(1, 2, 3, 4, 5), ids)
    }

    @Test
    fun `when search results are filtered then they return fewer characters than all characters`() = runTest {
        val allResult = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        val filteredResult = subject.getCharacterList(characterSearch = CharacterSearch(name = "smith"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "smith")))

        assertTrue(allResult.characters!!.size > filteredResult.characters!!.size)
        assertEquals(5, allResult.characters!!.size)
        assertEquals(4, filteredResult.characters!!.size)
    }

    @Test
    fun `when multiple calls are made then useCase provides consistent data`() = runTest {
        val call1 = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        val call2 = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))

        assertEquals(call1.characters!!.size, call2.characters!!.size)
        call1.characters!!.forEachIndexed { index, character ->
            assertEquals(character.id, call2.characters!![index].id)
            assertEquals(character.name, call2.characters!![index].name)
        }
    }

    @Test
    fun `given valid search when getCharacterList is called then no character fields are missing`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "rick")))
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
    fun `when all characters are fetched then all character IDs are unique`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = ""))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "")))
        val ids = result.characters!!.map { it.id }.toSet()
        assertEquals(result.characters!!.size, ids.size)
    }

    @Test
    fun `given species Human when getCharacterList is called then it returns only human characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(species = "Human"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(species = "Human")))
        assertFalse(result.hasError)
        assertTrue(result.characters!!.all { it.species == "Human" })
    }

    @Test
    fun `given species Human and name Rick when getCharacterList is called then it returns Rick Sanchez`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "Rick", species = "Human"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "Rick", species = "Human")))
        assertFalse(result.hasError)
        assertEquals(1, result.characters!!.size)
        assertEquals("Rick Sanchez", result.characters!![0].name)
        assertEquals("Human", result.characters!![0].species)
    }

    @Test
    fun `given species Alien when getCharacterList is called then it returns empty list`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(name = "Alien"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = "Alien")))
        assertFalse(result.hasError)
        assertTrue(result.characters!!.isEmpty())
    }

    @Test
    fun `given status Alive when getCharacterList is called then it returns only alive characters`() = runTest {
        val result = subject.getCharacterList(characterSearch = CharacterSearch(status = "Alive"))
        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(status = "Alive")))
        assertFalse(result.hasError)
        assertTrue(result.characters!!.all { it.status == "Alive" })
    }

    @Test
    fun `given search terms with different cases when getCharacterList is called then results are consistent`() = runTest {
        val searches = listOf("rick", "RICK", "Rick", "RiCk")

        searches.forEach { search ->
            val result = subject.getCharacterList(characterSearch = CharacterSearch(name = search))
            fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetCharacterList(characterSearch = CharacterSearch(name = search)))
            assertEquals(1, result.characters!!.size)
            assertEquals("Rick Sanchez", result.characters!![0].name)
        }
    }

    @Test
    fun `given valid url when getMoreCharacterList is called then it returns more characters`() = runTest {
        val url = "https://rickandmortyapi.com/api/character/?page=2"
        val result = subject.getMoreCharacterList(url = url)

        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetMoreCharacterList(url = url))
        assertFalse(result.hasError)
        assertNotNull(result.characters)
    }

    @Test
    fun `given empty url when getMoreCharacterList is called then it handles gracefully`() = runTest {
        val url = ""
        val result = subject.getMoreCharacterList(url = url)

        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetMoreCharacterList(url = url))
        assertFalse(result.hasError)
        assertEquals(5, result.characters!!.size)
    }

    @Test
    fun `given repository error when getMoreCharacterList is called then it propagates error`() = runTest {
        val url = "https://rickandmortyapi.com/api/character/?page=2"
        fakeRepository.setShouldReturnError(true, CharacterError.Unknown("Pagination error"))

        val result = subject.getMoreCharacterList(url = url)

        fakeRepository.verifyFunctionCalled(FakeCharacterListRepository.Function.GetMoreCharacterList(url = url))
        assertTrue(result.hasError)
        assertEquals(CharacterError.Unknown("Pagination error"), result.errorMsg)
    }
}
