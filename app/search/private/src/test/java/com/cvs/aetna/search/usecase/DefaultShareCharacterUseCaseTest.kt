package com.cvs.aetna.search.usecase

import android.content.Context
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.fake.FakeImageShareWrapper
import com.cvs.aetna.search.pub.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class DefaultShareCharacterUseCaseTest {
    lateinit var subject: DefaultShareCharacterUseCase

    @ApplicationContext
    private val applicationContextMock: Context = mock<Context>()
    private val imageShareWrapper: FakeImageShareWrapper = FakeImageShareWrapper()

    @Before
    fun setUp() {
        subject = DefaultShareCharacterUseCase(
            applicationContext = applicationContextMock,
            imageLoader = imageShareWrapper,
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `given valid character, when shareCharacterImage, then returns ShareData`() = runTest {
        mockString()
        val character = CharacterDetails(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            imageUrl = "url",
        )

        imageShareWrapper.testUri = "content://test-uri"

        val result = subject.shareCharacterImage(character)

        assertEquals("content://test-uri", result.fileUri)
        assertTrue(result.shareText.contains("Rick Sanchez"))
        assertTrue(result.shareText.contains("Alive"))
        assertTrue(result.shareText.contains("Human"))
    }

    @Test
    fun `given null data character, when shareCharacterImage, then returns ShareData`() = runTest {
        mockString()
        val character = CharacterDetails(
            id = 1,
            name = null,
            status = null,
            species = null,
            type = null,
            imageUrl = null,
        )

        imageShareWrapper.testUri = ""
        val result = subject.shareCharacterImage(character)
        assertEquals("", result.fileUri)
        assertTrue(result.shareText.contains("Unknown"))
    }

    @Test
    fun `given empty data character, when shareCharacterImage, then returns ShareData`() = runTest {
        mockString()
        val character = CharacterDetails(
            id = 1,
            name = "",
            status = "",
            species = "",
            type = "",
            imageUrl = "",
        )
        imageShareWrapper.testUri = ""
        val result = subject.shareCharacterImage(character)
        assertEquals("", result.fileUri)
        assertTrue(result.shareText.contains("Unknown"))
    }

    private fun mockString() {
        whenever(applicationContextMock.getString(R.string.character_details_unknown)).thenReturn("Unknown")
        whenever(applicationContextMock.getString(R.string.character_name)).thenReturn("Character Name:")
        whenever(applicationContextMock.getString(eq(R.string.character_details_status), any()))
            .thenAnswer { "Status: ${it.arguments[1]}" }

        whenever(applicationContextMock.getString(eq(R.string.character_details_species), any()))
            .thenAnswer { "Species: ${it.arguments[1]}" }

        whenever(applicationContextMock.getString(eq(R.string.character_details_type), any()))
            .thenAnswer { "Type: ${it.arguments[1]}" }
    }
}
