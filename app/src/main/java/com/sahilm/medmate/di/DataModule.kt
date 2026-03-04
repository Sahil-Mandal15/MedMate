package com.sahilm.medmate.di

import android.content.Context
import androidx.annotation.UiContext
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sahilm.medmate.data.local.database.MedMateDatabase
import com.sahilm.medmate.data.remote.FoodFactApi
import com.sahilm.medmate.data.repository.HomeRepositoryImpl
import com.sahilm.medmate.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideHttpLoggingInterceptor() = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: okhttp3.logging.HttpLoggingInterceptor) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    fun provideRetrofit(client: OkHttpClient) = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideFoodFactApi(retrofit: Retrofit) = retrofit.create(FoodFactApi::class.java)

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        MedMateDatabase::class.java,
        "app_database"
    ).build()

    @Provides
    fun provideHomeRepository(
        retrofit: FoodFactApi,
        database: MedMateDatabase
    ): HomeRepository = HomeRepositoryImpl(retrofit, database.foodFactDao())
}