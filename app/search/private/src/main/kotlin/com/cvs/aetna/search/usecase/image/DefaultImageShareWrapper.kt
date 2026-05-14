package com.cvs.aetna.search.usecase.image

import com.cvs.aetna.search.domain.usecase.FileProviderWrapper
import com.cvs.aetna.search.domain.usecase.FileWriter
import com.cvs.aetna.search.domain.usecase.ImageDownloader
import com.cvs.aetna.search.domain.usecase.ImageShareWrapper
import java.io.File
import javax.inject.Inject

class DefaultImageShareWrapper @Inject constructor(
    private val downloader: ImageDownloader,
    private val fileWriter: FileWriter,
    private val fileProvider: FileProviderWrapper,
) : ImageShareWrapper {

    override suspend fun loadImageFile(
        imageUrl: String,
        outputFile: File,
    ): String {
        val bytes = downloader.download(imageUrl)
        fileWriter.write(outputFile, bytes)
        return fileProvider.getUriForFile(outputFile)
    }
}
