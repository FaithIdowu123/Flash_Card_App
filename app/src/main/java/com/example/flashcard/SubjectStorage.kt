package com.example.flashcard

import android.content.Context
import org.json.JSONArray
import androidx.core.content.edit

class SubjectStorage(context: Context) {

    private val prefs = context.getSharedPreferences("subjects_prefs", Context.MODE_PRIVATE)

    fun saveSubjects(subjects: List<String>) {
        val jsonArray = JSONArray()
        subjects.forEach {
            jsonArray.put(it)
        }

        prefs.edit { putString("subjects", jsonArray.toString()) }
    }

    fun loadSubjects(): MutableList<String> {
        val jsonString = prefs.getString("subjects", null) ?: return mutableListOf()

        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }

        return list
    }
}