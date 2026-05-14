package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.usecase.FileProviderWrapper
import java.io.File

class FakeFileProviderWrapper :
    FileProviderWrapper,
    FakeFunctionHelper<FakeFileProviderWrapper.Function> {

    sealed class Function {
        data class GetUriForFile(val file: File) : Function()
    }

    var fakeUri: String? = ""
    override fun getUriForFile(file: File): String {
        recordCalledFunction(
            Function.GetUriForFile(
                file = file,
            ),
        )
        return fakeUri ?: ""
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
