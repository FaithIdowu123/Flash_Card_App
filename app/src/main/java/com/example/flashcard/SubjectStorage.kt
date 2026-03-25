package com.example.flashcard

import android.content.Context
import org.json.JSONArray
import androidx.core.content.edit

// Handles saving and loading subjects using SharedPreferences
class SubjectStorage(context: Context) {

    // SharedPreferences storage
    private val prefs = context.getSharedPreferences("subjects_prefs", Context.MODE_PRIVATE)

    fun saveSubjects(subjects: List<String>) {
        val jsonArray = JSONArray()

        // Convert list to JSON array
        subjects.forEach {
            jsonArray.put(it)
        }

        // Save JSON as string
        prefs.edit { putString("subjects", jsonArray.toString()) }
    }

    fun loadSubjects(): MutableList<String> {
        // Get stored JSON string or return empty list
        val jsonString = prefs.getString("subjects", null) ?: return mutableListOf()

        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<String>()

        // Convert JSON array to list
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }

        return list
    }
}