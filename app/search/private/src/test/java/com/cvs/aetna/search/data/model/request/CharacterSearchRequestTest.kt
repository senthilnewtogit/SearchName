package com.cvs.aetna.search.data.model.request

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterSearchRequestTest {

    @Test
    fun `given all fields when toQueryMap then returns map with lowercase status and species`() {
        val request = CharacterSearchRequest(
            name = "Rick",
            status = "Alive",
            species = "Human",
            type = "Scientist",
        )

        val queryMap = request.toQueryMap()

        assertEquals("Rick", queryMap["name"])
        assertEquals("alive", queryMap["status"])
        assertEquals("human", queryMap["species"])
        assertEquals("Scientist", queryMap["type"])
    }

    @Test
    fun `given empty fields when toQueryMap then returns empty map`() {
        val request = CharacterSearchRequest(
            name = "",
            status = null,
            species = null,
            type = "",
        )

        val queryMap = request.toQueryMap()

        assertTrue(queryMap.isEmpty())
    }

    @Test
    fun `given only species Human when toQueryMap then returns map with lowercase human`() {
        val request = CharacterSearchRequest(species = "Human")

        val queryMap = request.toQueryMap()

        assertEquals(1, queryMap.size)
        assertEquals("human", queryMap["species"])
    }

    @Test
    fun `given name with whitespace when toQueryMap then preserves whitespace if not blank`() {
        val request = CharacterSearchRequest(name = " Rick ")

        val queryMap = request.toQueryMap()

        assertEquals(" Rick ", queryMap["name"])
    }

    @Test
    fun `given blank name when toQueryMap then excludes name`() {
        val request = CharacterSearchRequest(name = "   ")

        val queryMap = request.toQueryMap()

        assertFalse(queryMap.containsKey("name"))
    }
}
