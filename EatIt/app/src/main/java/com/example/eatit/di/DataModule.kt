package com.example.eatit.di

import android.content.Context
import com.example.eatit.EatItApp
import com.example.eatit.data.PlacesRepository
import com.example.eatit.data.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideSettingsRepository(@ApplicationContext context: Context) = SettingsRepository(context)

    @Singleton
    @Provides
    fun providePlacesRepository(@ApplicationContext context: Context) =
        PlacesRepository((context.applicationContext as EatItApp).database.itemDAO())
}