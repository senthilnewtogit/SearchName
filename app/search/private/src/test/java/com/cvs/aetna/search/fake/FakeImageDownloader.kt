package com.cvs.aetna.search.fake

import com.cvs.aetna.search.domain.usecase.ImageDownloader

class FakeImageDownloader :
    ImageDownloader,
    FakeFunctionHelper<FakeImageDownloader.Function> {
    sealed class Function {
        data class Download(val url: String) : Function()
    }

    var fakeBytes = byteArrayOf(1, 2, 3)

    override suspend fun download(url: String): ByteArray {
        recordCalledFunction(Function.Download(url = url))
        return fakeBytes
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}
