package com.example.pdf_reader.objects

import android.content.Context
import android.net.Uri
import com.example.pdf_reader.models.RecentPdfModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RecentPdfManager {
    private const val PREFS_NAME = "recent_pdfs"
    private const val MAX_RECENT_FILES = 10

    fun saveRecentPdf(context: Context, uri: Uri, fileName: String) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recentFiles = getRecentPdfs(context).toMutableList()

        // Remove if already exists
        recentFiles.removeIf { it.uri == uri.toString() }

        // Add new entry
        recentFiles.add(0, RecentPdfModel(
            fileName = fileName,
            filePath = uri.path ?: "",
            lastOpened = System.currentTimeMillis(),
            uri = uri.toString()
        )
        )

        // Keep only last MAX_RECENT_FILES
        if (recentFiles.size > MAX_RECENT_FILES) {
            recentFiles.removeAt(recentFiles.size - 1)
        }

        // Save to SharedPreferences
        sharedPrefs.edit().apply {
            putString("recent_files", Gson().toJson(recentFiles))
            apply()
        }
    }

    fun getRecentPdfs(context: Context): List<RecentPdfModel> {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("recent_files", "[]")
        val type = object : TypeToken<List<RecentPdfModel>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
    fun updateRecentPdfs(context: Context, pdfs: List<RecentPdfModel>) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("recent_files", Gson().toJson(pdfs))
            apply()
        }
    }
}