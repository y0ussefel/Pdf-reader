package com.example.pdf_reader

import android.content.Context
import android.net.Uri
import kotlinx.serialization.json.Json

class RecentPdfManager private constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "recent_pdfs"
        private const val MAX_RECENT_FILES = 10

        @Volatile
        private var instance: RecentPdfManager? = null

        fun getInstance(context: Context): RecentPdfManager {
            return instance ?: synchronized(this) {
                instance ?: RecentPdfManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun addRecentPdf(uri: Uri, fileName: String) {
        val recentList = getRecentPdfs().toMutableList()

        // Remove if already exists to avoid duplicates
        recentList.removeIf { it.uri.toString() == uri.toString() }

        // Add new entry at the beginning
        recentList.add(0, RecentPdf(uri, fileName, System.currentTimeMillis()))

        // Keep only the most recent files
        if (recentList.size > MAX_RECENT_FILES) {
            recentList.removeAt(recentList.size - 1)
        }

        // Save updated list
        saveRecentList(recentList)
    }

    fun getRecentPdfs(): List<RecentPdf> {
        val recentJson = sharedPreferences.getString("recent_list", "[]")
        return try {
            Json.decodeFromString<List<RecentPdf>>(recentJson!!)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveRecentList(list: List<RecentPdf>) {
        val json = Json.encodeToString(list)
        sharedPreferences.edit().putString("recent_list", json).apply()
    }
}