package com.example.homework4.di

import android.content.Context
import androidx.room.Room
import com.example.homework4.data.local.AppDatabase
import com.example.homework4.data.local.FavouritesDao
import com.example.homework4.data.remote.CountriesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "countries_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDao(
        db: AppDatabase
    ): FavouritesDao {

        return db.favouritesDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCountriesApi(
        retrofit: Retrofit
    ): CountriesApi {

        return retrofit.create(CountriesApi::class.java)

    }
}