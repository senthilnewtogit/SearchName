package com.cvs.aetna.search.data.transformer

import com.cvs.aetna.search.data.model.response.Character
import com.cvs.aetna.search.data.model.response.Location
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
    fun `given character with all fields when transform then returns mapped character details`() {
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

        val result = transformer.transform(character)

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
    fun `given null character when transform then returns empty character details`() {
        val result = transformer.transform(null)

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
    fun `given character with whitespace fields when transform then trims values`() {
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

        val result = transformer.transform(character)

        assertEquals("Morty Smith", result.name)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertEquals("", result.type)
        assertEquals("Earth (C-137)", result.origin)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", result.imageUrl)
        assertEquals("Nov 04, 2017", result.createdAt)
    }

    @Test
    fun `given character with null fields when transform then returns null fields`() {
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

        val result = transformer.transform(character)

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
    fun `given character with empty strings when transform then keeps empty values`() {
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

        val result = transformer.transform(character)

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
    fun `given invalid created date when transform then returns original date string`() {
        val character = Character(
            id = 1,
            name = "Rick",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Location(name = "Earth", url = ""),
            location = Location(name = "Earth", url = ""),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = emptyList(),
            url = "",
            created = "invalid-date",
        )

        val result = transformer.transform(character)

        assertEquals("invalid-date", result.createdAt)
    }
}
