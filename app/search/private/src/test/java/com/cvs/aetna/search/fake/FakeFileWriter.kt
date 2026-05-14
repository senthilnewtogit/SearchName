package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.usecase.FileWriter
import java.io.File

class FakeFileWriter :
    FileWriter,
    FakeFunctionHelper<FakeFileWriter.Function> {

    sealed class Function {
        data class Write(val file: File, val data: ByteArray) : Function()
    }

    override suspend fun write(file: File, data: ByteArray) {
        recordCalledFunction(
            Function.Write(
                file = file,
                data = data,
            ),
        )
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
