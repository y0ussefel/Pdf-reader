package com.example.pdf_reader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdf_reader.models.RecentPdfModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentPdfAdapter(
    private val context: Context,
    private val onItemClick: (RecentPdfModel) -> Unit
) : RecyclerView.Adapter<RecentPdfAdapter.ViewHolder>() {

    private var pdfList = mutableListOf<RecentPdfModel>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.fileName)
        val lastOpened: TextView = itemView.findViewById(R.id.lastOpened)
        val pdfCard: CardView = itemView.findViewById(R.id.pdfCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pdf = pdfList[position]
        holder.fileName.text = pdf.fileName
        holder.lastOpened.text = formatDate(pdf.lastOpened)
        holder.pdfCard.setOnClickListener { onItemClick(pdf) }
    }

    override fun getItemCount(): Int = pdfList.size

    fun updateList(newList: List<RecentPdfModel>) {
        pdfList.clear()
        pdfList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}