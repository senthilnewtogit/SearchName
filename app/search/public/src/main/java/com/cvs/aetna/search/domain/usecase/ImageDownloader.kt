package com.cvs.aetna.search.domain.usecase

interface ImageDownloader {
    suspend fun download(url: String): ByteArray
}
