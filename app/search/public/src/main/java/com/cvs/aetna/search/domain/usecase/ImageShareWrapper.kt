package com.cvs.aetna.search.domain.usecase

import java.io.File

interface ImageShareWrapper {
    suspend fun loadImageFile(
        imageUrl: String,
        outputFile: File,
    ): String
}
