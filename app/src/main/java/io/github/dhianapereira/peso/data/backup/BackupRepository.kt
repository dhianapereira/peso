package io.github.dhianapereira.peso.data.backup

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.dhianapereira.peso.data.preferences.PreferencesRepository
import io.github.dhianapereira.peso.data.weight.WeightEntryDao
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BackupRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: WeightEntryDao,
    private val preferences: PreferencesRepository
) {
    suspend fun export(uri: String) = withContext(Dispatchers.IO) {
        val entries = dao.getAll()
        val root = JSONObject().put("format", "peso-backup").put("version", 2)
        preferences.goalGrams.first()?.let { root.put("goalGrams", it) }
        val items = JSONArray()
        entries.forEach {
            items.put(
                JSONObject().put("id", it.id).put("dateEpochDay", it.dateEpochDay)
                    .put("weightGrams", it.weightGrams).put("createdAt", it.createdAt)
                    .put("updatedAt", it.updatedAt)
            )
        }
        root.put("entries", items)
        val output = context.contentResolver.openOutputStream(uri.toUri(), "wt")
            ?: error("Cannot open output")
        OutputStreamWriter(output, Charsets.UTF_8).use { it.write(root.toString(2)) }
    }

    suspend fun import(uri: String) = withContext(Dispatchers.IO) {
        val input =
            context.contentResolver.openInputStream(uri.toUri()) ?: error("Cannot open input")
        val text = input.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
        require(text.length <= 10_000_000) { "Backup too large" }
        val root = JSONObject(text)
        require(root.getString("format") == "peso-backup")
        val version = root.getInt("version")
        require(version in 1..2)
        val array = root.getJSONArray("entries")
        require(array.length() <= 100_000)
        val ids = mutableSetOf<Long>()
        val entries = buildList {
            repeat(array.length()) { index ->
                val item = array.getJSONObject(index)
                val grams = item.getInt("weightGrams")
                require(grams in 1_000..500_000)
                val id = item.getLong("id")
                require(id > 0 && ids.add(id))
                add(
                    WeightEntryEntity(
                        id,
                        if (version == 1) {
                            Instant.ofEpochMilli(item.getLong("measuredAt"))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toEpochDay()
                        } else {
                            item.getLong("dateEpochDay")
                        },
                        grams,
                        item.getLong("createdAt"),
                        item.getLong("updatedAt")
                    )
                )
            }
        }
        val goal = if (root.has("goalGrams")) {
            root.getInt("goalGrams")
                .also { require(it in 1_000..500_000) }
        } else {
            null
        }
        dao.replaceAll(entries)
        preferences.setGoal(goal)
    }
}
