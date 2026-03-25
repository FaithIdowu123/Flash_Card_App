package com.example.flashcard

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class CardStorage(context: Context) {

    private val prefs = context.getSharedPreferences("cards_prefs", Context.MODE_PRIVATE)

    @SuppressLint("UseKtx")
    fun saveCards(subject: String, cards: List<Pair<String, String>>) {

        val allData = loadAll()

        val jsonArray = JSONArray()
        cards.forEach { (q, a) ->
            val obj = JSONObject()
            obj.put("q", q)
            obj.put("a", a)
            jsonArray.put(obj)
        }

        allData.put(subject, jsonArray)

        prefs.edit { putString("cards", allData.toString()) }
    }

    fun loadCards(subject: String): MutableList<Pair<String, String>> {

        val allData = loadAll()
        val result = mutableListOf<Pair<String, String>>()

        if (!allData.has(subject)) return result

        val jsonArray = allData.getJSONArray(subject)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(obj.getString("q") to obj.getString("a"))
        }

        return result
    }

    private fun loadAll(): JSONObject {
        val jsonString = prefs.getString("cards", null)
        return if (jsonString != null) JSONObject(jsonString) else JSONObject()
    }
}