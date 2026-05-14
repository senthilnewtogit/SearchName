package com.cvs.aetna.search.domain.usecase

import java.io.File

interface FileWriter {
    suspend fun write(file: File, data: ByteArray)
}
