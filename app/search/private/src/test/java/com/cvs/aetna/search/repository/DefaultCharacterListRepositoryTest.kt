package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.response.CharacterResponse
import com.cvs.aetna.search.data.transformer.CharacterResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
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
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

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
            )
        }
        repository = DefaultCharacterListRepository(
            fakeAPI,
            domainTransform,
            telemetryService = fakeTelemetryService,
        )
    }

    @Test
    fun `given empty name, when getCharacterList called, then return all characters`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(5, result.characters?.size)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given name rick, when getCharacterList called, then return Rick Sanchez`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Rick Sanchez", result.characters?.get(0)?.name)
        assertEquals(1, result.characters?.get(0)?.id)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given name morty, when getCharacterList called, then return Morty Smith`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "morty"))

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Morty Smith", result.characters?.get(0)?.name)
        assertEquals(2, result.characters?.get(0)?.id)
    }

    @Test
    fun `given name smith, when getCharacterList called, then return multiple Smith characters`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "smith"))

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
    fun `given name rick, when getCharacterList called, then return character details`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

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
    fun `given next page exists, when getCharacterList called, then return hasMorePage true`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))

        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.contains("page=") == true)
    }

    @Test
    fun `given name rick, when getCharacterList called, then results are not null`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertNotNull(result.characters)
    }

    @Test
    fun `given case insensitive name MORTY, when getCharacterList called, then find Morty`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "MORTY"))

        assertFalse(result.hasError)
        assertEquals(1, result.characters?.size)
        assertEquals("Morty Smith", result.characters?.get(0)?.name)
    }

    @Test
    fun `given name rick, when getCharacterList called, then strings are trimmed`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val character = result.characters?.get(0)
        assertEquals("Rick Sanchez", character?.name)
        assertEquals("Alive", character?.status)
    }

    @Test
    fun `given nonexistent name, when getCharacterList called, then return empty list`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "NonExistentCharacter"))

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(0, result.characters?.size)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given API returns error, when getCharacterList called, then set hasError true`() = runTest {
        fakeAPI.setShouldReturnError(true, "Not Found")

        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
    }

    @Test
    fun `given API returns error, when getCharacterList called, then populate error message`() = runTest {
        val errorMessage = "Server Error"
        fakeAPI.setShouldReturnError(true, errorMessage)

        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
        assertEquals(CharacterError.NoResultFound("Server Error"), result.errorMsg)
    }

    @Test
    fun `given API throws exception, when getCharacterList called, then set hasError true`() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw SSLException("Transform error")
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
        assertEquals(CharacterError.SSL("Transform error"), result.errorMsg)
    }

    @Test
    fun `given API throws exception, when getCharacterList called, then set error message`() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw ConnectException("Network connection failed")
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertEquals(CharacterError.NoInternet("Network connection failed"), result.errorMsg)
    }

    @Test
    fun `given exception without message, when getCharacterList called, then set unknown error`() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw UnknownHostException("Unknown error")
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertEquals(CharacterError.UnknownHost("Unknown error"), result.errorMsg)
    }

    @Test
    fun `given exception occurs, when getCharacterList called, then log telemetry event`() = runTest {
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw SocketTimeoutException(
                "API Failure",
            )
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        repositoryWithThrowingTransform.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                eventName = "CharacterListError",
                params = mapOf("error" to "API Failure"),
            ),
            1,
        )
    }

    @Test
    fun `given multiple calls with different names, when getCharacterList called, then return correct results`() = runTest {
        val rickResult = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        val mortyResult = repository.getCharacterList(characterSearch = CharacterSearch(name = "morty"))

        assertEquals(1, rickResult.characters?.size)
        assertEquals("Rick Sanchez", rickResult.characters?.get(0)?.name)

        assertEquals(1, mortyResult.characters?.size)
        assertEquals("Morty Smith", mortyResult.characters?.get(0)?.name)
    }

    @Test
    fun `given multiple calls with same name, when getCharacterList called, then return consistent results`() = runTest {
        val result1 = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))
        val result2 = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertEquals(result1.characters?.size, result2.characters?.size)
        assertEquals(
            result1.characters?.get(0)?.name,
            result2.characters?.get(0)?.name,
        )
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun `given error recovery, when getCharacterList called, then return successful results`() = runTest {
        fakeAPI.setShouldReturnError(true, "Error")

        val errorResult = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        fakeAPI.setShouldReturnError(false)

        val successResult = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
        assertEquals(1, successResult.characters?.size)
    }

    @Test
    fun `given empty name, when getCharacterList called, then all characters have required fields`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))

        result.characters?.forEach { character ->
            assertNotNull(character.id)
            assertNotNull(character.name)
            assertNotNull(character.status)
            assertNotNull(character.species)
            assertNotNull(character.imageUrl)
        }
    }

    @Test
    fun `given empty name, when getCharacterList called, then pagination info is correct`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))

        assertTrue(result.hasMorePage)
        assertNotNull(result.nextPageUrl)
        assertTrue(result.nextPageUrl?.isNotEmpty() == true)
    }

    @Test
    fun `given empty name, when getCharacterList called, then character ids are unique`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))

        val ids = result.characters?.map { it.id }
        assertEquals(ids?.size, ids?.distinct()?.size)
    }

    @Test
    fun `given name rick, when getCharacterList called, then status field is populated`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val character = result.characters?.get(0)
        assertEquals("Alive", character?.status)
    }

    @Test
    fun `given name rick, when getCharacterList called, then species field is populated`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val character = result.characters?.get(0)
        assertEquals("Human", character?.species)
    }

    @Test
    fun `given name rick, when getCharacterList called, then origin field is populated`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val character = result.characters?.get(0)
        assertEquals("Earth (C-137)", character?.origin)
    }

    @Test
    fun `given empty name, when getCharacterList called, then return same result as null name`() = runTest {
        val emptyResult = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))
        assertEquals(5, emptyResult.characters?.size)
    }

    @Test
    fun `given filtered name, when getCharacterList called, then search is filtered`() = runTest {
        val allResult = repository.getCharacterList(characterSearch = CharacterSearch(name = ""))
        val filteredResult = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue((allResult.characters?.size ?: 0) > (filteredResult.characters?.size ?: 0))
    }

    @Test
    fun `given name rick, when getCharacterList called, then transform is called with response`() = runTest {
        var transformCalled = false
        val trackingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList {
                transformCalled = true
                return CharacterList(
                    characters = listOf(),
                    hasMorePage = false,
                )
            }
        }
        val repositoryWithTracking = DefaultCharacterListRepository(
            fakeAPI,
            trackingTransform,
            telemetryService = fakeTelemetryService,
        )

        repositoryWithTracking.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(transformCalled)
    }

    @Test
    fun `given name rick, when getCharacterList called, then image urls are preserved`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val imageUrl = result.characters?.get(0)?.imageUrl
        assertNotNull(imageUrl)
        assertTrue(imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun `given name rick, when getCharacterList called, then created date is preserved`() = runTest {
        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        val createdAt = result.characters?.get(0)?.createdAt
        assertNotNull(createdAt)
        assertTrue(createdAt?.contains("2017") == true)
    }

    @Test
    fun `given valid url, when getMoreCharacterList called, then return characters`() = runTest {
        val url = "https://rickandmortyapi.com/api/character/?page=2"
        val result = repository.getMoreCharacterList(url = url)

        assertNotNull(result)
        assertFalse(result.hasError)
        assertTrue(result.characters?.isNotEmpty() == true)
    }

    @Test
    fun `given 429 error with retry-after header, when getCharacterList called, then return rate limited message`() = runTest {
        val headers = okhttp3.Headers.headersOf("retry-after", "10")
        fakeAPI.setShouldReturnError(true, "Too Many Requests", code = 429, responseHeaders = headers)

        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertEquals(CharacterError.RateLimit(retryAfterSeconds = 10), result.errorMsg)
    }

    @Test
    fun `given 1015 error without retry-after header, when getCharacterList called, then return default rate limited message`() = runTest {
        fakeAPI.setShouldReturnError(true, "Cloudflare Rate Limit", code = 1015)

        val result = repository.getCharacterList(characterSearch = CharacterSearch(name = "rick"))

        assertTrue(result.hasError)
        assertEquals(CharacterError.RateLimit(retryAfterSeconds = 5), result.errorMsg)
    }

    @Test
    fun `given exception in getMoreCharacterList, when called, then log telemetry event`() = runTest {
        val url = "https://rickandmortyapi.com/api/character/?page=2"
        val throwingTransform = object : CharacterResponseToDomainTransform {
            override fun transform(characterResponse: CharacterResponse?): CharacterList = throw IOException(
                "Pagination failed",
            )
        }
        val repositoryWithThrowingTransform = DefaultCharacterListRepository(
            fakeAPI,
            characterResponseToDomainTransform = throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        repositoryWithThrowingTransform.getMoreCharacterList(url = url)

        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                eventName = "CharacterListError",
                params = mapOf("error" to "Pagination failed"),
            ),
            1,
        )
    }
}
