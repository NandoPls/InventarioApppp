// MakeRequestAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class MakeRequestAdapter(
    private val context: Context,
    private val inventoryList: List<InventoryItem>,
    private val selectedItems: HashMap<String, Int>
) : RecyclerView.Adapter<MakeRequestAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val etQuantity: EditText = itemView.findViewById(R.id.etQuantity)
        val cbSelect: CheckBox = itemView.findViewById(R.id.cbSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_make_request, parent, false)
        return InventoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = inventoryList[position]
        holder.tvCodigo.text = "Código: ${currentItem.CODIGO}"
        holder.tvDescripcion.text = "Descripción: ${currentItem.DESCRIPCION}"
        holder.etQuantity.inputType = InputType.TYPE_CLASS_NUMBER
        holder.etQuantity.hint = "Cantidad"

        // Restaurar estado si el ítem ya fue seleccionado
        if (selectedItems.containsKey(currentItem.CODIGO)) {
            holder.cbSelect.isChecked = true
            holder.etQuantity.setText(selectedItems[currentItem.CODIGO].toString())
        } else {
            holder.cbSelect.isChecked = false
            holder.etQuantity.text.clear()
        }

        holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            val quantityText = holder.etQuantity.text.toString()
            val quantity = quantityText.toIntOrNull()
            if (isChecked && quantity != null && quantity > 0) {
                selectedItems[currentItem.CODIGO] = quantity
            } else if (isChecked) {
                Toast.makeText(context, "Ingrese una cantidad válida para ${currentItem.CODIGO}", Toast.LENGTH_SHORT).show()
                holder.cbSelect.isChecked = false
            } else {
                selectedItems.remove(currentItem.CODIGO)
            }
        }

        holder.etQuantity.setOnFocusChangeListener { _, _ ->
            if (holder.cbSelect.isChecked) {
                val quantityText = holder.etQuantity.text.toString()
                val quantity = quantityText.toIntOrNull()
                if (quantity != null && quantity > 0) {
                    selectedItems[currentItem.CODIGO] = quantity
                } else {
                    Toast.makeText(context, "Ingrese una cantidad válida para ${currentItem.CODIGO}", Toast.LENGTH_SHORT).show()
                    holder.cbSelect.isChecked = false
                    selectedItems.remove(currentItem.CODIGO)
                }
            }
        }
    }

    override fun getItemCount(): Int = inventoryList.size
}
