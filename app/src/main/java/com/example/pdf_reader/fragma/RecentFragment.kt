package com.example.pdf_reader.fragma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdf_reader.PdfActivity
import com.example.pdf_reader.R
import com.example.pdf_reader.RecentPdfAdapter
import com.example.pdf_reader.objects.RecentPdfManager

class RecentFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecentPdfAdapter
    private lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)

        setupRecyclerView()
        loadRecentPdfs()
    }

    private fun setupRecyclerView() {
        adapter = RecentPdfAdapter(requireContext()) { recentPdf ->
            openPdf(Uri.parse(recentPdf.uri))
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RecentFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadRecentPdfs() {
        val recentPdfs = RecentPdfManager.getRecentPdfs(requireContext())
        adapter.updateList(recentPdfs)

        emptyView.visibility = if (recentPdfs.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (recentPdfs.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openPdf(uri: Uri) {
        try {
            // Take persistent URI permission
            val contentResolver = requireContext().contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)

            // Create and start intent
            val intent = Intent(requireContext(), PdfActivity::class.java).apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Handle permission errors or invalid URIs
            Toast.makeText(
                context,
                "Unable to open PDF. The file might have been moved or deleted.",
                Toast.LENGTH_LONG
            ).show()

            // Remove the invalid PDF from recent list
            val recentPdfs = RecentPdfManager.getRecentPdfs(requireContext())
            val updatedList = recentPdfs.filter { it.uri != uri.toString() }
            RecentPdfManager.updateRecentPdfs(requireContext(), updatedList)
            loadRecentPdfs() // Refresh the list
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecentPdfs()
    }
}