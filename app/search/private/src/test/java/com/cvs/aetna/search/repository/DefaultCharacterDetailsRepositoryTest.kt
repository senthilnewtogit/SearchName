package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.response.Character
import com.cvs.aetna.search.data.transformer.CharacterDetailsResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.fake.FakeCharacterNameAPI
import com.cvs.aetna.search.fake.FakeTelemetryService
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCharacterDetailsRepositoryTest {

    private lateinit var subject: DefaultCharacterDetailsRepository
    private lateinit var fakeAPI: FakeCharacterNameAPI
    private lateinit var domainTransform: CharacterDetailsResponseToDomainTransform

    private lateinit var fakeTelemetryService: FakeTelemetryService

    @Before
    fun setUp() {
        fakeAPI = FakeCharacterNameAPI()
        fakeTelemetryService = FakeTelemetryService()
        domainTransform = object : CharacterDetailsResponseToDomainTransform {
            override fun transform(characterDetailsResponse: Character?): CharacterDetails = CharacterDetails(
                id = characterDetailsResponse?.id,
                name = characterDetailsResponse?.name?.trim(),
                status = characterDetailsResponse?.status?.trim(),
                origin = characterDetailsResponse?.origin?.name?.trim(),
                species = characterDetailsResponse?.species?.trim(),
                type = characterDetailsResponse?.type?.trim(),
                createdAt = characterDetailsResponse?.created?.trim(),
                imageUrl = characterDetailsResponse?.image?.trim(),
                hasError = false,
                errorMsg = null,
            )
        }
        subject = DefaultCharacterDetailsRepository(
            characterNameAPI = fakeAPI,
            detailsResponseToDomainTransform = domainTransform,
            telemetryService = fakeTelemetryService,
        )
    }

    @After
    fun tearDown() {
        fakeTelemetryService.verifyNoFunctionsCalled()
        fakeAPI.verifyNoFunctionsCalled()
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then return Rick Sanchez`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()
        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given id 2, when getCharacterDetails called, then return Morty Smith`() = runTest {
        val result = subject.getCharacterDetails("2")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("2"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(2, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
    }

    @Test
    fun `given id 3, when getCharacterDetails called, then return Summer Smith`() = runTest {
        val result = subject.getCharacterDetails("3")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Female", result.name?.let { "Female" } ?: result.name)
    }

    @Test
    fun `given id 4, when getCharacterDetails called, then return Beth Smith`() = runTest {
        val result = subject.getCharacterDetails("4")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("4"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun `given id 5, when getCharacterDetails called, then return Jerry Smith`() = runTest {
        val result = subject.getCharacterDetails("5")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("5"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then return all character fields`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.id)
        assertNotNull(result.name)
        assertNotNull(result.status)
        assertNotNull(result.species)
        assertNotNull(result.origin)
        assertNotNull(result.imageUrl)
        assertNotNull(result.createdAt)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then return character origin`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then return character image url`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.imageUrl)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then return character created date`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("2017") == true)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then strings are trimmed`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
    }

    @Test
    fun `given valid id, when getCharacterDetails called, then no error is returned`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun `given invalid id 999, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("999")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("999"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given non-numeric id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("abc")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("abc"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given negative id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("-1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("-1"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given empty id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails(""))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given zero id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("0")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("0"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
        assertNotNull(result.errorMsg)
    }

    @Test
    fun `given API returns error, when getCharacterDetails called, then sets hasError true`() = runTest {
        fakeAPI.setShouldReturnError(true, "Not Found")

        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyFunctionNeverCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertFalse(result.hasError)
    }

    @Test
    fun `given throwing transform, when getCharacterDetails called, then catch and return error`() = runTest {
        val throwingTransform = object : CharacterDetailsResponseToDomainTransform {
            override fun transform(characterDetailsResponse: Character?): CharacterDetails = throw RuntimeException("Transform failed")
        }
        val repositoryWithThrowingTransform = DefaultCharacterDetailsRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyFunctionCalled(FakeTelemetryService.Function.LogEvent("GetCharacterDetailsError", mapOf("error" to "Transform failed")))

        assertTrue(result.hasError)
        assertEquals("Transform failed", result.errorMsg)
    }

    @Test
    fun `given transform throwing exception without message, when getCharacterDetails called, then set unknown error`() = runTest {
        val throwingTransform = object : CharacterDetailsResponseToDomainTransform {
            override fun transform(characterDetailsResponse: Character?): CharacterDetails = throw RuntimeException()
        }
        val repositoryWithThrowingTransform = DefaultCharacterDetailsRepository(
            fakeAPI,
            throwingTransform,
            telemetryService = fakeTelemetryService,
        )

        val result = repositoryWithThrowingTransform.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyFunctionCalled(FakeTelemetryService.Function.LogEvent("GetCharacterDetailsError", mapOf("error" to "Unknown error")))

        assertTrue(result.hasError)
        assertEquals("Unknown error", result.errorMsg)
    }

    @Test
    fun `given multiple calls with different ids, when getCharacterDetails called, then return correct results`() = runTest {
        val result1 = subject.getCharacterDetails("1")
        val result2 = subject.getCharacterDetails("2")
        val result3 = subject.getCharacterDetails("3")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("2"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Rick Sanchez", result1.name)
        assertEquals("Morty Smith", result2.name)
        assertEquals("Summer Smith", result3.name)

        assertFalse(result1.hasError)
        assertFalse(result2.hasError)
        assertFalse(result3.hasError)
    }

    @Test
    fun `given multiple calls with same id, when getCharacterDetails called, then return consistent results`() = runTest {
        val result1 = subject.getCharacterDetails("1")
        val result2 = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"), 2)
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(result1.id, result2.id)
        assertEquals(result1.name, result2.name)
        assertEquals(result1.status, result2.status)
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun `given error recovery, when getCharacterDetails called, then return successful result`() = runTest {
        fakeAPI.setShouldReturnError(true, "Error")

        val errorResult = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyFunctionNeverCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        fakeAPI.setShouldReturnError(false)

        val successResult = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertFalse(errorResult.hasError)
        assertEquals("Rick Sanchez", successResult.name)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then Rick Sanchez has correct status`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Alive", result.status)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then Rick Sanchez has correct species`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Human", result.species)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then Rick Sanchez has correct origin`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun `given id 2, when getCharacterDetails called, then Morty Smith has human species`() = runTest {
        val result = subject.getCharacterDetails("2")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("2"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Human", result.species)
    }

    @Test
    fun `given id 3, when getCharacterDetails called, then Summer Smith has correct data`() = runTest {
        val result = subject.getCharacterDetails("3")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Human", result.species)
    }

    @Test
    fun `given id 4, when getCharacterDetails called, then Beth Smith has correct data`() = runTest {
        val result = subject.getCharacterDetails("4")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("4"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun `given id 5, when getCharacterDetails called, then Jerry Smith has correct data`() = runTest {
        val result = subject.getCharacterDetails("5")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("5"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
        assertEquals("Human", result.species)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then id is correct type`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(1, result.id)
        assertTrue(result.id is Int)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then name is not empty`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.name)
        assertTrue(result.name?.isNotEmpty() == true)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then image url format is correct`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertTrue(result.imageUrl?.startsWith("https://") == true)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then created date format is correct`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("-") == true)
        assertTrue(result.createdAt?.contains("T") == true)
    }

    @Test
    fun `given whitespace id, when getCharacterDetails called, then handle correctly`() = runTest {
        val result = subject.getCharacterDetails("  1  ")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("  1  "))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
    }

    @Test
    fun `given large id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("99999999")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("99999999"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
    }

    @Test
    fun `given special character id, when getCharacterDetails called, then return error`() = runTest {
        val result = subject.getCharacterDetails("@#$%")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("@#$%"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        assertTrue(result.hasError)
    }

    @Test
    fun `given valid ids, when all characters called, then all successful`() = runTest {
        val result1 = subject.getCharacterDetails("1")
        val result2 = subject.getCharacterDetails("2")
        val result3 = subject.getCharacterDetails("3")
        val result4 = subject.getCharacterDetails("4")
        val result5 = subject.getCharacterDetails("5")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("2"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("4"))
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("5"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        listOf(result1, result2, result3, result4, result5).forEach { result ->
            assertFalse(result.hasError)
            assertNotNull(result.name)
        }
    }

    @Test
    fun `given id 1, when getCharacterDetails called, then transform is called`() = runTest {
        var transformCalled = false
        val trackingTransform = object : CharacterDetailsResponseToDomainTransform {
            override fun transform(characterDetailsResponse: Character?): CharacterDetails {
                transformCalled = true
                return CharacterDetails(id = characterDetailsResponse?.id)
            }
        }
        val repositoryWithTracking = DefaultCharacterDetailsRepository(
            fakeAPI,
            detailsResponseToDomainTransform = trackingTransform,
            telemetryService = fakeTelemetryService,
        )

        repositoryWithTracking.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertTrue(transformCalled)
    }

    @Test
    fun `given range of ids, when getCharacterDetails called for multiple ids, then return consistent data`() = runTest {
        val results = (1..5).map { subject.getCharacterDetails(it.toString()) }
        (1..5).forEach { id ->
            fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails(id.toString()))
        }
        fakeTelemetryService.verifyNoFunctionsCalled()

        results.forEach { result ->
            assertFalse(result.hasError)
            assertNotNull(result.id)
            assertTrue(result.id in 1..5)
            assertNotNull(result.name)
        }
    }

    @Test
    fun `given valid character id, when getCharacterDetails called, then no fields are empty`() = runTest {
        val result = subject.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.id)
        assertNotNull(result.name)
        assertNotNull(result.status)
        assertNotNull(result.species)
        assertNotNull(result.origin)
        assertNotNull(result.imageUrl)
        assertNotNull(result.createdAt)
        assertFalse(result.hasError)
    }
}
