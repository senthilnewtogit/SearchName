package com.cvs.aetna.search.data.transformer

import com.cvs.aetna.search.data.model.Character
import com.cvs.aetna.search.data.model.CharacterResponse
import com.cvs.aetna.search.data.model.Location
import com.cvs.aetna.search.data.model.PaginationInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultCharacterListToDomainTransformTest {

    private lateinit var transformer: DefaultCharacterListToDomainTransform

    @Before
    fun setUp() {
        transformer = DefaultCharacterListToDomainTransform()
    }

    @Test
    fun transform_withValidCharacterResponse_returnsCorrectCharacterList() {
        // Arrange
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z",
        )

        val paginationInfo = PaginationInfo(
            count = 107,
            pages = 6,
            next = "https://rickandmortyapi.com/api/character/?page=2&name=rick",
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = listOf(character),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(1, result.characters?.size)
        assertTrue(result.hasMorePage)
        assertEquals("https://rickandmortyapi.com/api/character/?page=2&name=rick", result.nextPageUrl)
        assertNull(result.errorMsg)
        assertFalse(result.hasError)
    }

    @Test
    fun transform_withMultipleCharacters_mapsAllCharactersCorrectly() {
        // Arrange
        val characters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(name = "Earth (C-137)", url = ""),
                location = Location(name = "Citadel of Ricks", url = ""),
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                episode = emptyList(),
                url = "",
                created = "2017-11-04T18:48:46.250Z",
            ),
            Character(
                id = 2,
                name = "Morty Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(name = "Earth (C-137)", url = ""),
                location = Location(name = "Earth (C-137)", url = ""),
                image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
                episode = emptyList(),
                url = "",
                created = "2017-11-04T20:32:59.209Z",
            ),
            Character(
                id = 3,
                name = "Summer Smith",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Female",
                origin = Location(name = "Earth (C-137)", url = ""),
                location = Location(name = "Earth (C-137)", url = ""),
                image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
                episode = emptyList(),
                url = "",
                created = "2017-11-04T20:33:30.896Z",
            ),
        )

        val paginationInfo = PaginationInfo(
            count = 300,
            pages = 10,
            next = "https://rickandmortyapi.com/api/character/?page=2",
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = characters,
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(3, result.characters?.size)
        assertEquals("Rick Sanchez", result.characters?.get(0)?.name)
        assertEquals("Morty Smith", result.characters?.get(1)?.name)
        assertEquals("Summer Smith", result.characters?.get(2)?.name)
    }

    @Test
    fun transform_withNullResponse_returnsEmptyCharacterList() {
        // Act
        val result = transformer.transform(null)

        // Assert
        assertEquals(emptyList<Any>(), result.characters)
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
        assertNull(result.errorMsg)
        assertFalse(result.hasError)
    }

    @Test
    fun transform_withEmptyResults_returnsEmptyList() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 0,
            pages = 0,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = emptyList(),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(emptyList<Any>(), result.characters)
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun transform_withNextPageUrl_hasMorePageSetToTrue() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 50,
            pages = 3,
            next = "https://rickandmortyapi.com/api/character/?page=2",
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = emptyList(),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertTrue(result.hasMorePage)
        assertEquals("https://rickandmortyapi.com/api/character/?page=2", result.nextPageUrl)
    }

    @Test
    fun transform_withoutNextPageUrl_hasMorePageSetToFalse() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 50,
            pages = 1,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = emptyList(),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun transform_withResponseError_setsErrorFields() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 0,
            pages = 0,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = null,
            error = "Not Found",
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertTrue(result.hasError)
        assertEquals("Not Found", result.errorMsg)
        assertEquals(emptyList<Any>(), result.characters)
    }

    @Test
    fun transform_withNullResultsList_returnsEmptyList() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 0,
            pages = 0,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = null,
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(emptyList<Any>(), result.characters)
        assertFalse(result.hasMorePage)
    }

    @Test
    fun transform_withCharacterNamesContainingWhitespace_trimmedCorrectly() {
        // Arrange
        val characters = listOf(
            Character(
                id = 1,
                name = "  Rick Sanchez  ",
                status = "  Alive  ",
                species = "  Human  ",
                type = "",
                gender = "Male",
                origin = Location(name = "  Earth (C-137)  ", url = ""),
                location = Location(name = "Citadel of Ricks", url = ""),
                image = "https://example.com/image.jpeg",
                episode = emptyList(),
                url = "",
                created = "  2017-11-04T18:48:46.250Z  ",
            ),
        )

        val paginationInfo = PaginationInfo(
            count = 1,
            pages = 1,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = characters,
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(1, result.characters?.size)
        assertEquals("Rick Sanchez", result.characters?.get(0)?.name)
        assertEquals("Alive", result.characters?.get(0)?.status)
        assertEquals("Human", result.characters?.get(0)?.species)
    }

    @Test
    fun transform_withNullInfoObject_handlesGracefully() {
        // Arrange
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://example.com/image.jpeg",
            episode = emptyList(),
            url = "",
            created = "2017-11-04T18:48:46.250Z",
        )

        val characterResponse = CharacterResponse(
            info = null,
            results = listOf(character),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(1, result.characters?.size)
        assertFalse(result.hasMorePage)
        assertNull(result.nextPageUrl)
    }

    @Test
    fun transform_preservesCharacterIdCorrectly() {
        // Arrange
        val characters = listOf(
            Character(
                id = 42,
                name = "Test",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = Location(name = "Earth", url = ""),
                location = Location(name = "Earth", url = ""),
                image = "https://example.com/image.jpeg",
                episode = emptyList(),
                url = "",
                created = "2017-11-04T18:48:46.250Z",
            ),
        )

        val paginationInfo = PaginationInfo(
            count = 1,
            pages = 1,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = characters,
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(42, result.characters?.get(0)?.id)
    }

    @Test
    fun transform_multipleTransformsWithSameInput_returnsConsistentResults() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 100,
            pages = 5,
            next = "https://page2.com",
            prev = null,
        )

        val character = Character(
            id = 1,
            name = "Rick",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://example.com/image.jpeg",
            episode = emptyList(),
            url = "",
            created = "2017-11-04T18:48:46.250Z",
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = listOf(character),
        )

        // Act
        val result1 = transformer.transform(characterResponse)
        val result2 = transformer.transform(characterResponse)

        // Assert
        assertEquals(result1.characters?.size, result2.characters?.size)
        assertEquals(result1.hasMorePage, result2.hasMorePage)
        assertEquals(result1.nextPageUrl, result2.nextPageUrl)
        assertEquals(result1.hasError, result2.hasError)
    }

    @Test
    fun transform_responseWithPaginationData_preservesAllPaginationInfo() {
        // Arrange
        val paginationInfo = PaginationInfo(
            count = 827,
            pages = 42,
            next = "https://rickandmortyapi.com/api/character/?page=2",
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = emptyList(),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertTrue(result.hasMorePage)
        assertEquals("https://rickandmortyapi.com/api/character/?page=2", result.nextPageUrl)
        assertNull(result.errorMsg)
    }

    @Test
    fun transform_characterWithImageUrl_preservedInDomain() {
        // Arrange
        val imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        val character = Character(
            id = 1,
            name = "Rick",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = imageUrl,
            episode = emptyList(),
            url = "",
            created = "2017-11-04T18:48:46.250Z",
        )

        val paginationInfo = PaginationInfo(
            count = 1,
            pages = 1,
            next = null,
            prev = null,
        )

        val characterResponse = CharacterResponse(
            info = paginationInfo,
            results = listOf(character),
        )

        // Act
        val result = transformer.transform(characterResponse)

        // Assert
        assertEquals(imageUrl, result.characters?.get(0)?.imageUrl)
    }
}
