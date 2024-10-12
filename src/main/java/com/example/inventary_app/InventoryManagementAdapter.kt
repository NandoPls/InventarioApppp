// InventoryManagementAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.view.*
import android.widget.*

class InventoryManagementAdapter(private val context: Context, private val dataSource: ArrayList<InventoryItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size

    override fun getItem(position: Int): Any = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_inventory_management, parent, false)

        val tvCodigo = rowView.findViewById<TextView>(R.id.tvCodigo)
        val tvDescripcion = rowView.findViewById<TextView>(R.id.tvDescripcion)
        val tvStock = rowView.findViewById<TextView>(R.id.tvStock)
        val btnEdit = rowView.findViewById<Button>(R.id.btnEditItem)
        val btnDelete = rowView.findViewById<Button>(R.id.btnDeleteItem)

        val item = getItem(position) as InventoryItem

        tvCodigo.text = "Código: ${item.CODIGO}"
        tvDescripcion.text = "Descripción: ${item.DESCRIPCION}"
        tvStock.text = "Stock: ${item.stock}"

        btnEdit.setOnClickListener {
            (context as ManageInventoryActivity).showEditItemDialog(item)
        }

        btnDelete.setOnClickListener {
            (context as ManageInventoryActivity).deleteInventoryItem(item)
        }

        return rowView
    }
}
