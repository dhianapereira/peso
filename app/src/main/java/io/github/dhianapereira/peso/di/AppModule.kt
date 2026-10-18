package io.github.dhianapereira.peso.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.dhianapereira.peso.data.database.PesoDatabase
import io.github.dhianapereira.peso.data.weight.WeightEntryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): PesoDatabase = Room.databaseBuilder(context, PesoDatabase::class.java, "peso.db")
        .addMigrations(PesoDatabase.MIGRATION_1_2)
        .build()

    @Provides
    fun weightDao(database: PesoDatabase): WeightEntryDao = database.weightEntryDao()
}
