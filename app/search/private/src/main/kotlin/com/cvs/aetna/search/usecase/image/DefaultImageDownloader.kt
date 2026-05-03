package com.cvs.aetna.search.usecase.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.cvs.aetna.search.domain.usecase.ImageDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class DefaultImageDownloader @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context,
) : ImageDownloader {

    override suspend fun download(url: String): ByteArray {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request) as SuccessResult
        val drawable = result.image.asDrawable(context.resources)
        val bitmap = (drawable as BitmapDrawable).bitmap

        return bitmap.toByteArray()
    }
}
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
