package com.example.pdf_reader

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class RecentPdf(
    val uri: @Serializable(with = UriSerializer::class) Uri,
    val fileName: String,
    val timestamp: Long
)
