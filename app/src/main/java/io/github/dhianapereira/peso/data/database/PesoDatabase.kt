package io.github.dhianapereira.peso.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.dhianapereira.peso.data.weight.WeightEntryDao
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity

@Database(entities = [WeightEntryEntity::class], version = 2, exportSchema = true)
abstract class PesoDatabase : RoomDatabase() {
    abstract fun weightEntryDao(): WeightEntryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS weight_entries_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dateEpochDay INTEGER NOT NULL, weightGrams INTEGER NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("INSERT INTO weight_entries_new (id, dateEpochDay, weightGrams, createdAt, updatedAt) SELECT id, CAST(julianday(date(measuredAt / 1000, 'unixepoch', 'localtime')) - 2440587.5 AS INTEGER), weightGrams, createdAt, updatedAt FROM weight_entries")
                db.execSQL("DROP TABLE weight_entries")
                db.execSQL("ALTER TABLE weight_entries_new RENAME TO weight_entries")
            }
        }
    }
}
