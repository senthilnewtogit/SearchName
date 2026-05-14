package com.cvs.aetna.search.usecase

import com.cvs.aetna.search.fake.FakeFileProviderWrapper
import com.cvs.aetna.search.fake.FakeFileWriter
import com.cvs.aetna.search.fake.FakeImageDownloader
import com.cvs.aetna.search.usecase.image.DefaultImageShareWrapper
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class DefaultImageShareWrapperTest {

    private lateinit var subject: DefaultImageShareWrapper
    private val downloader: FakeImageDownloader = FakeImageDownloader()

    private val fileWriter: FakeFileWriter = FakeFileWriter()

    private val fileProvider: FakeFileProviderWrapper = FakeFileProviderWrapper()

    @Before
    fun setUp() {
        subject = DefaultImageShareWrapper(
            downloader = downloader,
            fileWriter = fileWriter,
            fileProvider = fileProvider,
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `given image url and file, when loadImageFile called, then download write and return uri`() = runTest {
        val file = File("test.png")
        val fakeBytes = byteArrayOf(1, 2, 3)
        downloader.fakeBytes = fakeBytes
        fileProvider.fakeUri = "content://uri"
        val result = subject.loadImageFile("url", file)
        downloader.verifyFunctionCalled(FakeImageDownloader.Function.Download("url"))
        fileWriter.verifyFunctionCalled(FakeFileWriter.Function.Write(file, fakeBytes))
        fileProvider.verifyFunctionCalled(FakeFileProviderWrapper.Function.GetUriForFile(file))
        assertEquals("content://uri", result)
    }
}
