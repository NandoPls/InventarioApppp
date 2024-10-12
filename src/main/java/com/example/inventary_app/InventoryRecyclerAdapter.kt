// InventoryRecyclerAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class InventoryRecyclerAdapter(
    private val context: Context,
    private var inventoryList: List<InventoryItem>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<InventoryRecyclerAdapter.InventoryViewHolder>(), Filterable {

    var inventoryListFull: List<InventoryItem> = ArrayList(inventoryList)

    interface OnItemClickListener {
        fun onItemClick(item: InventoryItem)
    }

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(inventoryList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_inventory_recycler, parent, false)
        return InventoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = inventoryList[position]
        holder.tvCodigo.text = "Código: ${currentItem.CODIGO}"
        holder.tvDescripcion.text = "Descripción: ${currentItem.DESCRIPCION}"
        holder.tvStock.text = "Stock: ${currentItem.stock}"
    }

    override fun getItemCount(): Int {
        return inventoryList.size
    }

    // Implementación de Filterable
    override fun getFilter(): Filter {
        return inventoryFilter
    }

    private val inventoryFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<InventoryItem>()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(inventoryListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (item in inventoryListFull) {
                    if (item.CODIGO.toLowerCase().contains(filterPattern) ||
                        item.DESCRIPCION.toLowerCase().contains(filterPattern) ||
                        item.CodigoSAP.toLowerCase().contains(filterPattern) ||
                        item.DescripcionSAP.toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            inventoryList = results?.values as List<InventoryItem>
            notifyDataSetChanged()
        }
    }
}
