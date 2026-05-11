package com.cvs.aetna.search.usecase.file

import android.content.Context
import androidx.core.content.FileProvider
import com.cvs.aetna.search.domain.usecase.FileProviderWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DefaultFileProviderWrapper @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : FileProviderWrapper {

    override fun getUriForFile(file: File): String = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file,
    ).toString()
}
