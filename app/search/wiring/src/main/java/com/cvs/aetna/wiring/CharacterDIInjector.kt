package com.cvs.aetna.wiring

import com.cvs.aetna.search.analytics.CharacterNameAdobeTagUseCase
import com.cvs.aetna.search.analytics.DefaultCharacterNameAdobeTagUseCase
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.logger.DefaultTelemetryService
import com.cvs.aetna.search.logger.TelemetryService
import com.cvs.aetna.search.usecase.DefaultCharacterDetailsUseCase
import com.cvs.aetna.search.usecase.DefaultCharacterListUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}

@Module
@InstallIn(SingletonComponent::class)
interface CharacterListBindingsModule {

    @Binds
    @Singleton
    fun getCharacterListUseCase(impl: DefaultCharacterListUseCase): CharacterListUseCase

    @Binds
    @Singleton
    fun getCharacterDetailsUseCase(impl: DefaultCharacterDetailsUseCase): CharacterDetailsUseCase

    @Binds
    @Singleton
    fun getTelemetryService(impl: DefaultTelemetryService): TelemetryService

    @Binds
    @Singleton
    fun getCharacterNameAnalyticsUseCase(impl: DefaultCharacterNameAdobeTagUseCase): CharacterNameAdobeTagUseCase
}
