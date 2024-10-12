// WorkerRequestsAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class WorkerRequestsAdapter(
    private val context: Context,
    private val requestsList: List<RequestItem>
) : RecyclerView.Adapter<WorkerRequestsAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRequestId: TextView = itemView.findViewById(R.id.tvRequestId)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvItems: TextView = itemView.findViewById(R.id.tvItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_worker_request, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val currentRequest = requestsList[position]
        holder.tvRequestId.text = "Solicitud ID: ${currentRequest.requestId}"
        holder.tvStatus.text = "Estado: ${currentRequest.status}"
        holder.tvItems.text = "Ítems: ${formatItems(currentRequest.items)}"
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    private fun formatItems(items: Map<String, Int>?): String {
        if (items == null) return ""
        val itemList = items.map { "${it.key}: ${it.value}" }
        return itemList.joinToString(", ")
    }
}
