package com.example.pdf_reader.fragma

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pdf_reader.PdfActivity
import com.example.pdf_reader.R


class BrowseFragment : Fragment() {
    lateinit var addBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBtn = view.findViewById(R.id.addPdf)
        addBtn.setOnClickListener {
            val intent =  Intent(requireContext(), PdfActivity::class.java)
            startActivity(intent)
        }
    }


}