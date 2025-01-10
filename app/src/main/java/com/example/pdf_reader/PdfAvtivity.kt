package com.example.pdf_reader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.barteksc.pdfviewer.PDFView

class PdfAvtivity : AppCompatActivity() {
    lateinit var pdfView : PDFView
    val PDF_SELECTION_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf_avtivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pdfView = findViewById(R.id.pdfView)
        selectPdfFromStorage()
    }

    private fun selectPdfFromStorage() {
        Toast.makeText(this, "Select PDF file", Toast.LENGTH_SHORT).show()
        val browserStorage = Intent(Intent.ACTION_GET_CONTENT)
        browserStorage.type = "application/pdf"
        browserStorage.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(browserStorage,"select pdf"),PDF_SELECTION_CODE)
    }

    fun showPdfFromUri(uri : Uri?){
        pdfView.fromUri(uri)
            .defaultPage(0)
            .spacing(0)
            .load()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null){
            val selctedPdf = data.data
            showPdfFromUri(selctedPdf)
        }
    }
}