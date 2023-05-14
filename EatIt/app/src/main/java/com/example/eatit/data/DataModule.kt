package com.example.eatit.data

import android.content.Context
import com.example.eatit.EatItApp
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