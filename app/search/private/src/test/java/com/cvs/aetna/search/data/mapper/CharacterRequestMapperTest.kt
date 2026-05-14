package com.cvs.aetna.search.data.mapper

import com.cvs.aetna.search.domain.model.CharacterSearch
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterRequestMapperTest {

    @Test
    fun `given CharacterSearch with all fields when toRequest then returns CharacterSearchRequest with same fields`() {
        val domain = CharacterSearch(
            name = "Morty",
            status = "Alive",
            species = "Human",
            type = "Sidekick",
        )

        val request = domain.toRequest()

        assertEquals("Morty", request.name)
        assertEquals("Alive", request.status)
        assertEquals("Human", request.species)
        assertEquals("Sidekick", request.type)
    }

    @Test
    fun `given CharacterSearch with null fields when toRequest then returns CharacterSearchRequest with null fields`() {
        val domain = CharacterSearch(
            name = null,
            status = null,
            species = null,
            type = null,
        )

        val request = domain.toRequest()

        assertEquals(null, request.name)
        assertEquals(null, request.status)
        assertEquals(null, request.species)
        assertEquals(null, request.type)
    }
}
