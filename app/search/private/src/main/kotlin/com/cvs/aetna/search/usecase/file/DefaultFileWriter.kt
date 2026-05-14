package com.cvs.aetna.search.usecase.file

import com.cvs.aetna.search.di.IoDispatcher
import com.cvs.aetna.search.domain.usecase.FileWriter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DefaultFileWriter @Inject constructor(
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileWriter {

    override suspend fun write(file: File, data: ByteArray) {
        withContext(dispatcher) {
            FileOutputStream(file).use {
                it.write(data)
            }
        }
    }
}
