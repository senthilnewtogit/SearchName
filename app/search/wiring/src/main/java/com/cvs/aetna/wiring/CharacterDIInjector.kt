package com.cvs.aetna.wiring

import android.content.Context
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.cvs.aetna.search.analytics.CharacterDetailsAdobeTagUseCase
import com.cvs.aetna.search.analytics.CharacterListAdobeTagUseCase
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.domain.usecase.FileProviderWrapper
import com.cvs.aetna.search.domain.usecase.FileWriter
import com.cvs.aetna.search.domain.usecase.ImageDownloader
import com.cvs.aetna.search.domain.usecase.ImageShareWrapper
import com.cvs.aetna.search.domain.usecase.ShareCharacterUseCase
import com.cvs.aetna.search.logger.DefaultTelemetryService
import com.cvs.aetna.search.logger.TelemetryService
import com.cvs.aetna.search.usecase.DefaultCharacterDetailsUseCase
import com.cvs.aetna.search.usecase.DefaultCharacterListUseCase
import com.cvs.aetna.search.usecase.DefaultShareCharacterUseCase
import com.cvs.aetna.search.usecase.analytics.DefaultCharacterDetailsAdobeTagUseCase
import com.cvs.aetna.search.usecase.analytics.DefaultCharacterListAdobeTagUseCase
import com.cvs.aetna.search.usecase.file.DefaultFileProviderWrapper
import com.cvs.aetna.search.usecase.file.DefaultFileWriter
import com.cvs.aetna.search.usecase.image.DefaultImageDownloader
import com.cvs.aetna.search.usecase.image.DefaultImageShareWrapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CharacterDIInjector {

    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface CharacterListBindingsModule {

    @Binds
    @Singleton
    fun bindCharacterListUseCase(impl: DefaultCharacterListUseCase): CharacterListUseCase

    @Binds
    @Singleton
    fun bindCharacterDetailsUseCase(impl: DefaultCharacterDetailsUseCase): CharacterDetailsUseCase

    @Binds
    @Singleton
    fun bindTelemetryService(impl: DefaultTelemetryService): TelemetryService

    @Binds
    @Singleton
    fun bindCharacterListAnalyticsUseCase(impl: DefaultCharacterListAdobeTagUseCase): CharacterListAdobeTagUseCase

    @Binds
    @Singleton
    fun bindCharacterDetailsAnalyticsUseCase(impl: DefaultCharacterDetailsAdobeTagUseCase): CharacterDetailsAdobeTagUseCase

    @Binds
    @Singleton
    fun bindShareCharacterUseCase(impl: DefaultShareCharacterUseCase): ShareCharacterUseCase
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageShareBindings {

    @Singleton
    @Binds
    abstract fun bindImageShareWrapper(
        impl: DefaultImageShareWrapper,
    ): ImageShareWrapper

    @Singleton
    @Binds
    abstract fun bindImageDownloader(
        impl: DefaultImageDownloader,
    ): ImageDownloader

    @Singleton
    @Binds
    abstract fun bindFileWriter(
        impl: DefaultFileWriter,
    ): FileWriter

    @Singleton
    @Binds
    abstract fun bindFileProvider(
        impl: DefaultFileProviderWrapper,
    ): FileProviderWrapper
}
