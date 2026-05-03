package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.usecase.ImageShareWrapper
import com.cvs.aetna.search.fake.FakeImageShareWrapper.Function
import java.io.File

class FakeImageShareWrapper :
    ImageShareWrapper,
    FakeFunctionHelper<Function> {

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()

    sealed class Function {
        data class LoadImageFile(
            val imageUrl: String?,
            val outputFile: File,
        ) : Function()
    }

    var testUri: String? = null
    override suspend fun loadImageFile(
        imageUrl: String,
        outputFile: File,
    ): String {
        recordCalledFunction(Function.LoadImageFile(imageUrl = imageUrl, outputFile = outputFile))
        return testUri ?: ""
    }
}
