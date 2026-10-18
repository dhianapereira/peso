package io.github.dhianapereira.peso.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dhianapereira.peso.data.backup.BackupRepository
import io.github.dhianapereira.peso.data.preferences.PreferencesRepository
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.data.weight.WeightRepository
import io.github.dhianapereira.peso.model.AppTheme
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppEvent {
    data object Saved : AppEvent
    data object Deleted : AppEvent
    data class Backup(val success: Boolean, val importing: Boolean) : AppEvent
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeightRepository,
    private val preferences: PreferencesRepository,
    private val backup: BackupRepository
) : ViewModel() {
    val entries: StateFlow<List<WeightEntryEntity>> = repository.entries.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )
    val goalGrams: StateFlow<Int?> =
        preferences.goalGrams.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val theme: StateFlow<AppTheme> = preferences.theme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppTheme.SYSTEM
    )
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()
    fun save(id: Long?, dateEpochDay: Long, grams: Int) = viewModelScope.launch {
        repository.save(
            id,
            dateEpochDay,
            grams
        )
        _events.emit(AppEvent.Saved)
    }

    fun delete(entry: WeightEntryEntity) = viewModelScope.launch {
        repository.delete(entry)
        _events.emit(AppEvent.Deleted)
    }

    fun setGoal(grams: Int?) = viewModelScope.launch { preferences.setGoal(grams) }
    fun setTheme(value: AppTheme) = viewModelScope.launch { preferences.setTheme(value) }
    fun export(uri: String) = viewModelScope.launch {
        _events.emit(
            AppEvent.Backup(
                runCatching { backup.export(uri) }.isSuccess,
                false
            )
        )
    }

    fun import(uri: String) = viewModelScope.launch {
        _events.emit(
            AppEvent.Backup(
                runCatching { backup.import(uri) }.isSuccess,
                true
            )
        )
    }
}
