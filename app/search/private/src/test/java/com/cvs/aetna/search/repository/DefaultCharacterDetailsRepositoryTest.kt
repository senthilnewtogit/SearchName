package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.model.Character
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

    private lateinit var repository: DefaultCharacterDetailsRepository
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
        repository = DefaultCharacterDetailsRepository(
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
    fun getCharacterDetails_withId1_returnsRickSanchez() = runTest {
        val result = repository.getCharacterDetails("1")
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
    fun getCharacterDetails_withId2_returnsMortySmith() = runTest {
        val result = repository.getCharacterDetails("2")
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
    fun getCharacterDetails_withId3_returnsSummerSmith() = runTest {
        val result = repository.getCharacterDetails("3")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Female", result.name?.let { "Female" } ?: result.name)
    }

    @Test
    fun getCharacterDetails_withId4_returnsBethSmith() = runTest {
        val result = repository.getCharacterDetails("4")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("4"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun getCharacterDetails_withId5_returnsJerrySmith() = runTest {
        val result = repository.getCharacterDetails("5")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("5"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result)
        assertFalse(result.hasError)
        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
    }

    @Test
    fun getCharacterDetails_returnsAllCharacterFields() = runTest {
        val result = repository.getCharacterDetails("1")
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
    fun getCharacterDetails_returnsCharacterOrigin() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun getCharacterDetails_returnsCharacterImageUrl() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.imageUrl)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun getCharacterDetails_returnsCharacterCreatedDate() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("2017") == true)
    }

    @Test
    fun getCharacterDetails_stringsAreTrimmed() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Rick Sanchez", result.name) // Should be trimmed
        assertEquals("Alive", result.status) // Should be trimmed
    }

    @Test
    fun getCharacterDetails_noErrorForValidId() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun getCharacterDetails_withInvalidId999_returnsError() = runTest {
        val result = repository.getCharacterDetails("999")
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
    fun getCharacterDetails_withNonNumericId_returnsError() = runTest {
        val result = repository.getCharacterDetails("abc")
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
    fun getCharacterDetails_withNegativeId_returnsError() = runTest {
        val result = repository.getCharacterDetails("-1")
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
    fun getCharacterDetails_withEmptyId_returnsError() = runTest {
        val result = repository.getCharacterDetails("")
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
    fun getCharacterDetails_withZeroId_returnsError() = runTest {
        val result = repository.getCharacterDetails("0")
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
    fun getCharacterDetails_whenAPIReturnsError_setsHasErrorTrue() = runTest {
        fakeAPI.setShouldReturnError(true, "Not Found")

        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
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
    fun getCharacterDetails_whenAPIReturnsError_populatesErrorMessage() = runTest {
        val errorMessage = "Character not found"
        fakeAPI.setShouldReturnError(true, errorMessage)

        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
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
    fun getCharacterDetails_whenTransformThrowsException_catchesAndReturnsError() = runTest {
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
    fun getCharacterDetails_whenTransformThrowsExceptionWithoutMessage_setsUnknownError() = runTest {
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
    fun getCharacterDetails_multipleCallsWithDifferentIds_returnCorrectResults() = runTest {
        val result1 = repository.getCharacterDetails("1")
        val result2 = repository.getCharacterDetails("2")
        val result3 = repository.getCharacterDetails("3")
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
    fun getCharacterDetails_multipleCallsWithSameId_returnConsistentResults() = runTest {
        val result1 = repository.getCharacterDetails("1")
        val result2 = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"), 2)
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(result1.id, result2.id)
        assertEquals(result1.name, result2.name)
        assertEquals(result1.status, result2.status)
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun getCharacterDetails_afterErrorRecovery_returnSuccessfulResult() = runTest {
        fakeAPI.setShouldReturnError(true, "Error")

        val errorResult = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyFunctionCalled(
            FakeTelemetryService.Function.LogEvent(
                "GetCharacterDetailsError",
                mapOf(
                    "error" to "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path \$\n" +
                        "See https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure",
                ),
            ),
        )

        fakeAPI.setShouldReturnError(false)

        val successResult = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertTrue(errorResult.hasError)
        assertFalse(successResult.hasError)
        assertEquals("Rick Sanchez", successResult.name)
    }

    @Test
    fun getCharacterDetails_rickSanchez_hasCorrectStatus() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Alive", result.status)
    }

    @Test
    fun getCharacterDetails_rickSanchez_hasCorrectSpecies() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Human", result.species)
    }

    @Test
    fun getCharacterDetails_rickSanchez_hasCorrectOrigin() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Earth (C-137)", result.origin)
    }

    @Test
    fun getCharacterDetails_mortySmith_hasHumanSpecies() = runTest {
        val result = repository.getCharacterDetails("2")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("2"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals("Human", result.species)
    }

    @Test
    fun getCharacterDetails_summerSmith_hasCorrectData() = runTest {
        val result = repository.getCharacterDetails("3")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("3"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(3, result.id)
        assertEquals("Summer Smith", result.name)
        assertEquals("Human", result.species)
    }

    @Test
    fun getCharacterDetails_bethSmith_hasCorrectData() = runTest {
        val result = repository.getCharacterDetails("4")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("4"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(4, result.id)
        assertEquals("Beth Smith", result.name)
    }

    @Test
    fun getCharacterDetails_jerrySmith_hasCorrectData() = runTest {
        val result = repository.getCharacterDetails("5")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("5"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(5, result.id)
        assertEquals("Jerry Smith", result.name)
        assertEquals("Human", result.species)
    }

    @Test
    fun getCharacterDetails_idIsCorrectType() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertEquals(1, result.id)
        assertTrue(result.id is Int)
    }

    @Test
    fun getCharacterDetails_nameIsNotEmpty() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.name)
        assertTrue(result.name?.isNotEmpty() == true)
    }

    @Test
    fun getCharacterDetails_imageUrlFormatIsCorrect() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertTrue(result.imageUrl?.startsWith("https://") == true)
        assertTrue(result.imageUrl?.contains("rickandmortyapi.com") == true)
    }

    @Test
    fun getCharacterDetails_createdDateFormatIsCorrect() = runTest {
        val result = repository.getCharacterDetails("1")
        fakeAPI.verifyFunctionCalled(FakeCharacterNameAPI.Function.GetCharacterDetails("1"))
        fakeTelemetryService.verifyNoFunctionsCalled()

        assertNotNull(result.createdAt)
        assertTrue(result.createdAt?.contains("-") == true)
        assertTrue(result.createdAt?.contains("T") == true)
    }

    @Test
    fun getCharacterDetails_withWhitespaceId_isHandledCorrectly() = runTest {
        val result = repository.getCharacterDetails("  1  ")
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
    fun getCharacterDetails_withLargeId_returnsError() = runTest {
        val result = repository.getCharacterDetails("99999999")
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
    fun getCharacterDetails_withSpecialCharacterId_returnsError() = runTest {
        val result = repository.getCharacterDetails("@#$%")
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
    fun getCharacterDetails_callAllCharacters_allSuccessful() = runTest {
        val result1 = repository.getCharacterDetails("1")
        val result2 = repository.getCharacterDetails("2")
        val result3 = repository.getCharacterDetails("3")
        val result4 = repository.getCharacterDetails("4")
        val result5 = repository.getCharacterDetails("5")
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
    fun getCharacterDetails_transformIsCalled() = runTest {
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
    fun getCharacterDetails_allCharactersReturnConsistentData() = runTest {
        val results = (1..5).map { repository.getCharacterDetails(it.toString()) }
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
    fun getCharacterDetails_noFieldsAreEmpty_forValidCharacter() = runTest {
        val result = repository.getCharacterDetails("1")
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
