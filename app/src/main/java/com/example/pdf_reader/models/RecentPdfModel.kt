package com.example.pdf_reader.models

data class RecentPdfModel(
    val fileName: String,
    val filePath: String,
    val lastOpened: Long,
    val uri: String
)
