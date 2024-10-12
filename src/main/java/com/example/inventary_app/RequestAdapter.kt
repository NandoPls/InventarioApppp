// RequestAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestAdapter(
    private val context: Context,
    private val requestsList: List<RequestItem>
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple_request, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val currentRequest = requestsList[position]
        holder.tvItemName.text = "Item: ${currentRequest.itemName}"
        holder.tvQuantity.text = "Cantidad: ${currentRequest.quantity}"
        holder.tvStatus.text = "Estado: ${currentRequest.status}"
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }
}
