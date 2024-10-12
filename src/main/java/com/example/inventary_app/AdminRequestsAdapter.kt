// AdminRequestsAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminRequestsAdapter(
    private val context: Context,
    private val requestsList: MutableList<RequestItem>
) : RecyclerView.Adapter<AdminRequestsAdapter.RequestViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRequestItemName: TextView = itemView.findViewById(R.id.tvRequestItemName)
        val tvRequestQuantity: TextView = itemView.findViewById(R.id.tvRequestQuantity)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnApproveRequest: Button = itemView.findViewById(R.id.btnApproveRequest)
        val btnRejectRequest: Button = itemView.findViewById(R.id.btnRejectRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_request, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestsList[position]

        holder.tvRequestItemName.text = "${request.userName} solicita ${request.itemName}"
        holder.tvRequestQuantity.text = "Cantidad: ${request.quantity}"
        holder.tvStatus.text = "Estado: ${request.status}"

        holder.btnApproveRequest.setOnClickListener {
            updateRequestStatus(request, "approved", position)
        }

        holder.btnRejectRequest.setOnClickListener {
            updateRequestStatus(request, "rejected", position)
        }
    }

    override fun getItemCount(): Int = requestsList.size

    private fun updateRequestStatus(request: RequestItem, status: String, position: Int) {
        val requestRef = db.collection("requests").document(request.requestId)
        requestRef.update("status", status).addOnSuccessListener {
            if (status == "approved") {
                updateInventory(request)
            } else if (status == "rejected") {
                // Opcional: Si la solicitud es rechazada, puedes devolver el stock si fue descontado previamente
            }
            Toast.makeText(context, "Solicitud $status", Toast.LENGTH_SHORT).show()
            requestsList.removeAt(position)
            notifyItemRemoved(position)
            // Enviar correo electrónico aquí si es necesario
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error al actualizar solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateInventory(request: RequestItem) {
        val inventoryRef = db.collection("inventory").document(request.itemId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(inventoryRef)
            val currentStock = snapshot.getLong("stock") ?: 0L
            val newStock = currentStock - request.quantity
            if (newStock >= 0) {
                transaction.update(inventoryRef, "stock", newStock)
            } else {
                throw Exception("Cantidad insuficiente en inventario")
            }
        }.addOnSuccessListener {
            // Éxito al actualizar el inventario
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error al actualizar inventario: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
