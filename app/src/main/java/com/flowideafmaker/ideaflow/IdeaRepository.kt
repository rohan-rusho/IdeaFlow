package com.flowideafmaker.ideaflow

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IdeaRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("IdeaFlowPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "saved_ideas"

    fun saveIdeas(ideas: List<Idea>) {
        val json = gson.toJson(ideas)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun loadIdeas(): List<Idea>? {
        val json = sharedPreferences.getString(key, null) ?: return null
        val type = object : TypeToken<List<Idea>>() {}.type
        return gson.fromJson(json, type)
    }
}