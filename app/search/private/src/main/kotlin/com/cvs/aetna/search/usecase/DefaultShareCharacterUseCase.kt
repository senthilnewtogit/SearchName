package com.cvs.aetna.search.usecase

import android.content.Context
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.ShareData
import com.cvs.aetna.search.domain.usecase.ImageShareWrapper
import com.cvs.aetna.search.domain.usecase.ShareCharacterUseCase
import com.cvs.aetna.search.pub.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

private const val SHARED_IMAGE_PNG = "shared_image.png"

class DefaultShareCharacterUseCase @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    private val imageLoader: ImageShareWrapper,
) : ShareCharacterUseCase {
    override suspend fun shareCharacterImage(
        character: CharacterDetails,
    ): ShareData {
        val file = File(applicationContext.cacheDir, SHARED_IMAGE_PNG)
        val uri = character.imageUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { imageLoader.loadImageFile(it, file) }

        val unknown = applicationContext.getString(R.string.character_details_unknown)
        val shareText = buildString {
            appendLine(applicationContext.getString(R.string.character_name))
            appendLine(character.name ?: unknown)
            appendLine(
                applicationContext.getString(
                    R.string.character_details_status,
                    character.status ?: unknown,
                ),
            )
            appendLine(
                applicationContext.getString(
                    R.string.character_details_species,
                    character.species ?: unknown,
                ),
            )
            appendLine(
                applicationContext.getString(
                    R.string.character_details_type,
                    character.type?.takeIf { it.isNotBlank() } ?: unknown,
                ),
            )
        }

        return ShareData(fileUri = uri ?: "", shareText = shareText)
    }
}
