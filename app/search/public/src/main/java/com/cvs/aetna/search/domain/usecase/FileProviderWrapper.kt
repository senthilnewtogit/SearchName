package com.cvs.aetna.search.domain.usecase

import java.io.File

interface FileProviderWrapper {
    fun getUriForFile(file: File): String
}
