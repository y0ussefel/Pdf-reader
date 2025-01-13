package com.example.pdf_reader.fragma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdf_reader.R
import com.example.pdf_reader.RecentPdfManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecentPdfManager
    private lateinit var recentManager: RecentPdfManager

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
        recentManager = RecentPdfManager.getInstance(requireContext())

        setupRecyclerView()
        loadRecentPdfs()
    }

    private fun setupRecyclerView() {
        adapter = RecentPdfAdapter { recentPdf ->
            openPdf(recentPdf.uri)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RecentsFragment.adapter
        }
    }

    private fun loadRecentPdfs() {
        val recentPdfs = recentManager.getRecentPdfs()
        adapter.submitList(recentPdfs)
    }

    private fun openPdf(uri: Uri) {
        val intent = Intent(requireContext(), PdfActivity::class.java).apply {
            data = uri
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadRecentPdfs() // Refresh list when returning to fragment
    }
}