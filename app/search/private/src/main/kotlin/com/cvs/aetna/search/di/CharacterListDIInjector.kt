package com.cvs.aetna.search.di

import com.cvs.aetna.search.data.remote.CharacterNameAPI
import com.cvs.aetna.search.data.transformer.CharacterDetailsResponseToDomainTransform
import com.cvs.aetna.search.data.transformer.CharacterResponseToDomainTransform
import com.cvs.aetna.search.data.transformer.DefaultCharacterListToDomainTransform
import com.cvs.aetna.search.data.transformer.DefaultCharacterToDomainTransform
import com.cvs.aetna.search.domain.repo.CharacterDetailsRepository
import com.cvs.aetna.search.domain.repo.CharacterListRepository
import com.cvs.aetna.search.repository.DefaultCharacterDetailsRepository
import com.cvs.aetna.search.repository.DefaultCharacterListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CharacterListDIInjector {

    @Provides
    @Singleton
    fun privateCharacterListAPI(): CharacterNameAPI = Retrofit.Builder().baseUrl("https://rickandmortyapi.com").addConverterFactory(
        GsonConverterFactory.create(),
    ).build()
        .create(CharacterNameAPI::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface CharacterListBindingsModule {

    @Binds
    @Singleton
    fun getCharacterListToDomainTransform(impl: DefaultCharacterListToDomainTransform): CharacterResponseToDomainTransform

    @Binds
    @Singleton
    fun getCharacterListRepository(impl: DefaultCharacterListRepository): CharacterListRepository

    @Binds
    @Singleton
    fun getCharacterDetailsToDomainTransform(impl: DefaultCharacterToDomainTransform): CharacterDetailsResponseToDomainTransform

    @Binds
    @Singleton
    fun getCharacterDetailsRepository(impl: DefaultCharacterDetailsRepository): CharacterDetailsRepository
}
