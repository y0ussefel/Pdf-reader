package com.example.pdf_reader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.barteksc.pdfviewer.PDFView

class PdfAvtivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView
    private lateinit var recentManager: RecentPdfManager
    private val PDF_SELECTION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf_avtivity)

        pdfView = findViewById(R.id.pdfView)
        recentManager = RecentPdfManager.getInstance(this)

        // Check if PDF URI was passed from recent list
        intent.data?.let { uri ->
            showPdfFromUri(uri)
        } ?: selectPdfFromStorage()
    }

    private fun selectPdfFromStorage() {
        Toast.makeText(this, "Select PDF file", Toast.LENGTH_SHORT).show()
        val browserStorage = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(
            Intent.createChooser(browserStorage, "Select PDF"),
            PDF_SELECTION_CODE
        )
    }

    private fun showPdfFromUri(uri: Uri?) {
        uri?.let {
            pdfView.fromUri(it)
                .defaultPage(0)
                .spacing(0)
                .load()

            // Add to recent files
            getFileName(it)?.let { fileName ->
                recentManager.addRecentPdf(it, fileName)
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdf = data.data
            showPdfFromUri(selectedPdf)
        }
    }
}
