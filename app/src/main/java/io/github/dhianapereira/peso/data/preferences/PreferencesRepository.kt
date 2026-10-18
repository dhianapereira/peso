package io.github.dhianapereira.peso.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.dhianapereira.peso.model.AppTheme
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("preferences")

class PreferencesRepository @Inject constructor(@param:ApplicationContext private val context: Context) {
    val theme: Flow<AppTheme> = context.dataStore.data.map { value ->
        value[THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.SYSTEM
    }
    val goalGrams: Flow<Int?> = context.dataStore.data.map { it[GOAL] }
    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[THEME] = theme.name }
    }

    suspend fun setGoal(grams: Int?) {
        context.dataStore.edit { values ->
            if (grams == null) values.remove(GOAL) else values[GOAL] = grams
        }
    }

    private companion object {
        val THEME = stringPreferencesKey("theme")
        val GOAL = intPreferencesKey("goal_grams")
    }
}
