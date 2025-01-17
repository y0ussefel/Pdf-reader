package com.example.pdf_reader

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import android.text.InputType
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import com.example.pdf_reader.objects.RecentPdfManager

class PdfActivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView
    private val PDF_SELECTION_CODE = 100
    private var currentPage = 0
    private var isEditMode = false
    private var isHighlightMode = false
    private var currentUri: Uri? = null

    // Add data structures to store annotations
    private val textAnnotations = mutableListOf<TextAnnotation>()

    // Data class for text annotations
    data class TextAnnotation(
        val text: String,
        val x: Float = 100f,  // Default x position
        val y: Float = 100f,  // Default y position
        val page: Int,
        val color: Int = Color.BLACK
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf_avtivity)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pdfView = findViewById(R.id.pdfView)

        val receivedUri = intent.data
        if (receivedUri != null) {
            // Open the PDF directly if URI was passed
            showPdfFromUri(receivedUri)
        } else {
            // If no URI was passed, show storage picker
            selectPdfFromStorage()
        }
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
        currentUri = uri
        pdfView.fromUri(uri)
            .defaultPage(currentPage)
            .onPageChange { page, pageCount ->
                currentPage = page
                title = String.format("%s %s / %s", "Page", page + 1, pageCount)
            }
            .onDraw { canvas, pageWidth, pageHeight, displayedPage ->
                drawAnnotations(canvas, displayedPage)
            }
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10)
            .pageFitPolicy(FitPolicy.WIDTH)
            .load()
            uri?.let {
                val fileName = getFileName(it) // You'll need to implement this
                RecentPdfManager.saveRecentPdf(this, it, fileName)
            }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "Unknown PDF"
    }

    private fun drawAnnotations(canvas: Canvas, displayedPage: Int) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 36f
            isAntiAlias = true
        }

        // Draw all text annotations for the current page
        textAnnotations.filter { it.page == displayedPage }.forEach { annotation ->
            canvas.drawText(annotation.text, annotation.x, annotation.y, paint)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_text -> {
                showAddTextDialog()
                true
            }
            R.id.action_highlight -> {
                toggleHighlightMode()
                true
            }
            R.id.action_draw -> {
                toggleDrawMode()
                true
            }
            R.id.action_save -> {
                savePdfWithAnnotations()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddTextDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Add Text Annotation")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val text = input.text.toString()
                addTextAnnotation(text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addTextAnnotation(text: String) {
        // Create a new text annotation for the current page
        val annotation = TextAnnotation(
            text = text,
            page = currentPage,
            x = 100f,  // You can modify these coordinates
            y = 100f   // to position the text where you want
        )

        // Add to our list of annotations
        textAnnotations.add(annotation)

        // Refresh the view to show the new annotation
        pdfView.invalidate()
    }

    private fun toggleHighlightMode() {
        isHighlightMode = !isHighlightMode
        isEditMode = false
    }

    private fun toggleDrawMode() {
        isEditMode = !isEditMode
        isHighlightMode = false
    }

    private fun savePdfWithAnnotations() {
        Toast.makeText(this, "Saving PDF with annotations...", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdf = data.data
            showPdfFromUri(selectedPdf)
        }
    }
}