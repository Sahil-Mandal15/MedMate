package com.sahilm.medmate.di

import android.content.Context
import androidx.room.Room
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideFoodFactApi(retrofit: Retrofit): FoodFactApi = retrofit.create(FoodFactApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MedMateDatabase =
        Room.databaseBuilder(
            context,
            MedMateDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    @Singleton
    fun provideHomeRepository(
        api: FoodFactApi,
        database: MedMateDatabase
    ): HomeRepository = HomeRepositoryImpl(api, database.foodFactDao(), database.medicineReminderDao())
}