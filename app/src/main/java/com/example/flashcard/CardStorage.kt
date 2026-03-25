package com.example.flashcard

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

// Handles saving and loading flashcards using SharedPreferences
class CardStorage(context: Context) {

    // SharedPreferences storage
    private val prefs = context.getSharedPreferences("cards_prefs", Context.MODE_PRIVATE)

    @SuppressLint("UseKtx")
    fun saveCards(subject: String, cards: List<Pair<String, String>>) {

        val allData = loadAll() // load existing data

        val jsonArray = JSONArray()

        // Convert cards to JSON
        cards.forEach { (q, a) ->
            val obj = JSONObject()
            obj.put("q", q) // question
            obj.put("a", a) // answer
            jsonArray.put(obj)
        }

        // Save cards under subject
        allData.put(subject, jsonArray)

        // Store updated JSON string
        prefs.edit { putString("cards", allData.toString()) }
    }

    fun loadCards(subject: String): MutableList<Pair<String, String>> {

        val allData = loadAll() // load all stored data
        val result = mutableListOf<Pair<String, String>>() // output list

        // Return empty if subject not found
        if (!allData.has(subject)) return result

        val jsonArray = allData.getJSONArray(subject)

        // Convert JSON to list
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(obj.getString("q") to obj.getString("a"))
        }

        return result
    }

    // Load all stored JSON data
    private fun loadAll(): JSONObject {
        val jsonString = prefs.getString("cards", null)
        return if (jsonString != null) JSONObject(jsonString) else JSONObject()
    }
}