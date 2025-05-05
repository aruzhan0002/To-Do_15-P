package com.example.myapplication.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.time.LocalDate


val Context.dataStore by preferencesDataStore(name = "task_data")

object DataStoreHelper {
    private val TASKS_KEY = stringPreferencesKey("tasks")
    private val gson = Gson()

    suspend fun saveTasks(context: Context, taskMap: Map<LocalDate, List<MainActivity.Task>>) {
        val stringMap = taskMap.mapKeys { it.key.toString() }
        val jsonString = gson.toJson(stringMap)
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = jsonString
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadTasks(context: Context): MutableMap<LocalDate, MutableList<MainActivity.Task>> {
        val prefs = context.dataStore.data.first()
        val json = prefs[TASKS_KEY] ?: return mutableMapOf()

        val type = object : TypeToken<Map<String, List<MainActivity.Task>>>() {}.type
        val stringMap: Map<String, List<MainActivity.Task>> = gson.fromJson(json, type)

        return stringMap.mapKeys { LocalDate.parse(it.key) }
            .mapValues { it.value.toMutableList() }
            .toMutableMap()
    }
}
