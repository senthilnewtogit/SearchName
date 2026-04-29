package com.cvs.aetna.search.data.transformer

import com.cvs.aetna.search.data.model.Character
import com.cvs.aetna.search.data.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultCharacterToDomainTransformTest {

    private lateinit var transformer: DefaultCharacterToDomainTransform

    @Before
    fun setUp() {
        transformer = DefaultCharacterToDomainTransform()
    }

    @Test
    fun transform_withAllFieldsPopulated_returnsCharacterDetailsWithCorrectMapping() {
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

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Earth (C-137)", result.origin)
        assertEquals("Human", result.species)
        assertEquals("", result.type)
        assertEquals("Nov 04, 2017", result.createdAt)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", result.imageUrl)
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun transform_withNullInput_returnsCharacterDetailsWithAllNullFields() {
        // Act
        val result = transformer.transform(null)

        // Assert
        assertNull(result.id)
        assertNull(result.name)
        assertNull(result.status)
        assertNull(result.origin)
        assertNull(result.species)
        assertNull(result.type)
        assertNull(result.createdAt)
        assertNull(result.imageUrl)
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun transform_withWhitespaceStrings_trimmedCorrectly() {
        // Arrange
        val character = Character(
            id = 2,
            name = "  Morty Smith  ",
            status = "  Alive  ",
            species = "  Human  ",
            type = "  ",
            gender = "Male",
            origin = Location(name = "  Earth (C-137)  ", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
            image = "  https://rickandmortyapi.com/api/character/avatar/2.jpeg  ",
            episode = emptyList(),
            url = "https://rickandmortyapi.com/api/character/2",
            created = "  2017-11-04T18:48:46.250Z  ",
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertEquals("", result.type)
        assertEquals("Earth (C-137)", result.origin)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", result.imageUrl)
        assertEquals("Nov 04, 2017", result.createdAt)
    }

    @Test
    fun transform_withNullFields_returnsNullForThatField() {
        // Arrange
        val character = Character(
            id = 3,
            name = null,
            status = null,
            species = null,
            type = null,
            gender = "Male",
            origin = Location(name = null, url = ""),
            location = Location(name = "Citadel of Ricks", url = ""),
            image = null,
            episode = emptyList(),
            url = "https://rickandmortyapi.com/api/character/3",
            created = null,
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals(3, result.id)
        assertNull(result.name)
        assertNull(result.status)
        assertNull(result.species)
        assertNull(result.type)
        assertNull(result.origin)
        assertNull(result.imageUrl)
        assertNull(result.createdAt)
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun transform_withEmptyStrings_keepsEmpty() {
        // Arrange
        val character = Character(
            id = 4,
            name = "",
            status = "",
            species = "",
            type = "",
            gender = "Male",
            origin = Location(name = "", url = ""),
            location = Location(name = "", url = ""),
            image = "",
            episode = emptyList(),
            url = "",
            created = "",
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals(4, result.id)
        assertEquals("", result.name)
        assertEquals("", result.status)
        assertEquals("", result.species)
        assertEquals("", result.type)
        assertEquals("", result.origin)
        assertEquals("", result.imageUrl)
        assertEquals("", result.createdAt)
    }

    @Test
    fun transform_withMultipleEpisodes_succeeds() {
        // Arrange
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth (C-137)", url = ""),
            location = Location(name = "Citadel of Ricks", url = ""),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2",
                "https://rickandmortyapi.com/api/episode/3",
            ),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z",
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals("Rick Sanchez", result.name)
        assertEquals(1, result.id)
        // Episode list is not mapped to Result, but this ensures the transformation still works
    }

    @Test
    fun transform_hasErrorAndErrorMsgFields_setToFalseAndNull() {
        // Arrange
        val character = Character(
            id = 5,
            name = "Test Character",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://example.com/image.jpg",
            episode = emptyList(),
            url = "https://example.com",
            created = "2017-11-04T18:48:46.250Z",
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertFalse(result.hasError)
        assertNull(result.errorMsg)
    }

    @Test
    fun transform_withSpecialCharactersInName_preservedCorrectly() {
        // Arrange
        val character = Character(
            id = 6,
            name = "Rick & Morty's \"Adventure\" (C-137)",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://example.com/image.jpg",
            episode = emptyList(),
            url = "https://example.com",
            created = "2017-11-04T18:48:46.250Z",
        )

        // Act
        val result = transformer.transform(character)

        // Assert
        assertEquals("Rick & Morty's \"Adventure\" (C-137)", result.name)
    }

    @Test
    fun transform_multipleTransforms_returnsConsistentResults() {
        // Arrange
        val character = Character(
            id = 7,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Female",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://example.com/image.jpg",
            episode = emptyList(),
            url = "https://example.com",
            created = "2017-11-04T18:48:46.250Z",
        )

        // Act
        val result1 = transformer.transform(character)
        val result2 = transformer.transform(character)

        // Assert
        assertEquals(result1, result2)
    }
}
