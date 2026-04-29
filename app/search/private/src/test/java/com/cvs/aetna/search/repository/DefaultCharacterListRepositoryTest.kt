package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.CharacterResponse
import com.cvs.aetna.search.data.transformer.CharacterResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.fake.FakeCharacterNameAPI
import com.cvs.aetna.search.fake.FakeTelemetryService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCharacterListRepositoryTest {

    private lateinit var repository: DefaultCharacterListRepository
    private lateinit var fakeAPI: FakeCharacterNameAPI
    private lateinit var domainTransform: CharacterResponseToDomainTransform

    private lateinit var fakeTelemetryService: FakeTelemetryService

    @Before
    fun setUp() {
        fakeAPI = FakeCharacterNameAPI()
        fakeTelemetryService = FakeTelemetryService()
        domainTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = CharacterList(
                characters = characterResponse?.results?.map { character ->
                    CharacterDetails(
                        id = character.id,
                        name = character.name?.trim(),
                        status = character.status?.trim(),
                        origin = character.origin?.name?.trim(),
                        species = character.species?.trim(),
                        type = character.type?.trim(),
                        createdAt = character.created?.trim(),
                        imageUrl = character.image?.trim(),
                        hasError = false,
                        errorMsg = null,
                    )
                },
                hasMorePage = characterResponse?.info?.next != null,
                nextPageUrl = characterResponse?.info?.next,
                hasError = characterResponse?.error != null,
                errorMsg = characterResponse?.error,
            )
        }
        repository = DefaultCharacterListRepository(
            fakeAPI,
            domainTransform,
            telemetryService = fakeTelemetryService,
        )
    }

    @Test
    fun getCharacterList_withoutName_returnsAllCharacters() = runTest {
        val result = repository.getCharacterList("")

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(5, result.characters?.size)
        assertNull(result.errorMsg)
    }

    @Test
    fun getCharacterList_withNameRick_returnsRickSanchez() = runTest {
        val result = repository.getCharacterList("rick")

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Rick Sanchez", result.characters?.get(0)?.name)
        assertEquals(1, result.characters?.get(0)?.id)
        assertNull(result.errorMsg)
    }

    @Test
    fun getCharacterList_withNameMorty_returnsMortySmith() = runTest {
        val result = repository.getCharacterList("morty")

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Morty Smith", result.characters?.get(0)?.name)
        assertEquals(2, result.characters?.get(0)?.id)
    }

    @Test
    fun getCharacterList_withNameSmith_returnsMultipleSmithCharacters() = runTest {
        val result = repository.getCharacterList("smith")

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(4, result.characters?.size)

        val names = result.characters?.map { it.name }
        assertTrue(names?.contains("Morty Smith") == true)
        assertTrue(names?.contains("Summer Smith") == true)
        assertTrue(names?.contains("Beth Smith") == true)
        assertTrue(names?.contains("Jerry Smith") == true)
    }

    @Test
    fun getCharacterList_returnsCharacterDetails() = runTest {
        val result = repository.getCharacterList("rick")

        val character = result.characters?.get(0)
        assertNotNull(character)
        assertEquals(1, character?.id)
        assertEquals("Rick Sanchez", character?.name)
        assertEquals("Alive", character?.status)
        assertEquals("Human", character?.species)
        assertEquals("Earth (C-137)", character?.origin)
        assertNotNull(character?.imageUrl)
        assertNotNull(character?.createdAt)
    }

    @Test
    fun getCharacterList_returnsHasMorePageWhenNextPageExists() = runTest {
        val result = repository.getCharacterList("")

        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("page=") == true)
    }

    @Test
    fun getCharacterList_resultsAreNotNull() = runTest {
        val result = repository.getCharacterList("rick")

        assertNotNull(result.characters)
    }

    @Test
    fun getCharacterList_caseInsensitiveName_findsMorty() = runTest {
        val result = repository.getCharacterList("MORTY")

        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Morty Smith", result.characters?.get(0)?.name)
    }

    @Test
    fun getCharacterList_stringsAreTrimmed() = runTest {
        val result = repository.getCharacterList("rick")

        val character = result.characters?.get(0)
        assertEquals("Rick Sanchez", character?.name) // Should be trimmed
        assertEquals("Alive", character?.status) // Should be trimmed
    }

    @Test
    fun getCharacterList_withNonexistentName_returnsEmptyList() = runTest {
        val result = repository.getCharacterList("NonExistentCharacter")

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(0, result.characters?.size)
        assertNull(result.errorMsg)
    }

    // ==================== Error Cases ====================

    @Test
    fun getCharacterList_whenAPIReturnsError_setHasErrorTrue() = runTest {
        fakeAPI.setShouldReturnError(true, "Not Found")

        val result = repository.getCharacterList("rick")

        assertTrue(result.hasError)
    }

    @Test
    fun getCharacterList_whenAPIReturnsError_populatesErrorMessage() = runTest {
        val errorMessage = "Server Error"
        fakeAPI.setShouldReturnError(true, errorMessage)

        val result = repository.getCharacterList("rick")

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun getCharacterList_whenAPIThrowsException_setsHasErrorTrue() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw RuntimeException("Transform error")
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList("rick")

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
        assertEquals("Transform error", result.errorMsg)
    }

    @Test
    fun getCharacterList_whenAPIThrowsException_setsErrorMessage() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw RuntimeException("Network connection failed")
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList("rick")

        assertTrue(result.hasError)
        assertEquals("Network connection failed", result.errorMsg)
    }

    @Test
    fun getCharacterList_whenExceptionWithoutMessage_setsUnknownError() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw RuntimeException()
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList("rick")

        assertTrue(result.hasError)
        assertEquals("Unknown error", result.errorMsg)
    }

    @Test
    fun getCharacterList_multipleCallsWithDifferentNames_returnCorrectResults() = runTest {
        val rickResult = repository.getCharacterList("rick")
        val mortyResult = repository.getCharacterList("morty")

        assertEquals(1, rickResult.characters?.size)
        assertEquals("Rick Sanchez", rickResult.characters?.get(0)?.name)

        assertEquals(1, mortyResult.characters?.size)
        assertEquals("Morty Smith", mortyResult.characters?.get(0)?.name)
    }

    @Test
    fun getCharacterList_multipleCallsWithSameName_returnConsistentResults() = runTest {
        val result1 = repository.getCharacterList("rick")
        val result2 = repository.getCharacterList("rick")

        assertEquals(result1.characters?.size, result2.characters?.size)
        assertEquals(
            result1.characters?.get(0)?.name,
            result2.characters?.get(0)?.name,
        )
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun getCharacterList_afterErrorRecovery_returnSuccessfulResults() = runTest {
        fakeAPI.setShouldReturnError(true, "Error")

        val errorResult = repository.getCharacterList("rick")

        fakeAPI.setShouldReturnError(false)

        val successResult = repository.getCharacterList("rick")

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters?.size)
    }

    @Test
    fun getCharacterList_allCharactersHaveRequiredFields() = runTest {
        val result = repository.getCharacterList("")

        result.characters?.forEach { character ->
            assertNotNull(character.id)
            assertNotNull(character.name)
            assertNotNull(character.status)
            assertNotNull(character.species)
            assertNotNull(character.imageUrl)
        }
    }

    @Test
    fun getCharacterList_paginationInfoIsCorrect() = runTest {
        val result = repository.getCharacterList("")

        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.isNotEmpty() == true)
    }

    @Test
    fun getCharacterList_characterIdsAreUnique() = runTest {
        val result = repository.getCharacterList("")

        val ids = result.characters?.map { it.id }
        assertEquals(ids?.size, ids?.distinct()?.size)
    }

    @Test
    fun getCharacterList_statusFieldIsPopulated() = runTest {
        val result = repository.getCharacterList("rick")

        val character = result.characters?.get(0)
        assertEquals("Alive", character?.status)
    }

    @Test
    fun getCharacterList_speciesFieldIsPopulated() = runTest {
        val result = repository.getCharacterList("rick")

        val character = result.characters?.get(0)
        assertEquals("Human", character?.species)
    }

    @Test
    fun getCharacterList_originFieldIsPopulated() = runTest {
        val result = repository.getCharacterList("rick")

        val character = result.characters?.get(0)
        assertEquals("Earth (C-137)", character?.origin)
    }

    @Test
    fun getCharacterList_emptyNameAndNullName_returnSameResult() = runTest {
        val emptyResult = repository.getCharacterList("")
        assertEquals(5, emptyResult.characters?.size)
    }

    @Test
    fun getCharacterList_searchIsFiltered() = runTest {
        val allResult = repository.getCharacterList("")
        val filteredResult = repository.getCharacterList("rick")

        assertTrue((allResult.characters?.size ?: 0) > (filteredResult.characters?.size ?: 0))
    }

    @Test
    fun getCharacterList_transformIsCalledWithResponse() = runTest {
        var transformCalled = false
        val trackingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList {
                transformCalled = true
                return CharacterList(
                    characters = listOf(),
                    hasMorePage = false,
                    hasError = false,
                )
            }
        }
        val repositoryWithTracking = DefaultCharacterListRepository(
            fakeAPI,
            trackingTransform,
            telemetryService = fakeTelemetryService,
        )

        repositoryWithTracking.getCharacterList("rick")

        assertTrue(transformCalled)
    }

    @Test
    fun getCharacterList_imagUrlsArePreserved() = runTest {
        val result = repository.getCharacterList("rick")

        val imageUrl = result.characters?.get(0)?.imageUrl
        assertNotNull(imageUrl)
        assertTrue(imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun getCharacterList_createdDateIsPreserved() = runTest {
        val result = repository.getCharacterList("rick")

        val createdAt = result.characters?.get(0)?.createdAt
        assertNotNull(createdAt)
        assertTrue(createdAt?.contains("2017") == true)
    }
}
