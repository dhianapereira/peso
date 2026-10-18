package io.github.dhianapereira.peso.data.weight

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WeightRepository @Inject constructor(private val dao: WeightEntryDao) {
    val entries: Flow<List<WeightEntryEntity>> = dao.observeAll()
    suspend fun save(id: Long?, dateEpochDay: Long, weightGrams: Int) {
        val now = System.currentTimeMillis()
        if (id == null) {
            dao.insert(
                WeightEntryEntity(
                    dateEpochDay = dateEpochDay,
                    weightGrams = weightGrams,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            dao.getById(id)?.let {
                dao.update(
                    it.copy(
                        dateEpochDay = dateEpochDay,
                        weightGrams = weightGrams,
                        updatedAt = now
                    )
                )
            }
        }
    }

    suspend fun delete(entry: WeightEntryEntity) = dao.delete(entry)
}
