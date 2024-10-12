// InventoryAdapter.kt
package com.example.inventary_app

import android.content.Context
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class InventoryAdapter(private val context: Context, private val dataSource: ArrayList<InventoryItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun getCount(): Int { return dataSource.size }

    override fun getItem(position: Int): Any { return dataSource[position] }

    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.activity_list_inventory, parent, false)

        // Obtener referencias a los TextViews
        val codigoTextView = rowView.findViewById<TextView>(R.id.tvCodigo)
        val descripcionTextView = rowView.findViewById<TextView>(R.id.tvDescripcion)
        val codigoSAPTextView = rowView.findViewById<TextView>(R.id.tvCodigoSAP)
        val descripcionSAPTextView = rowView.findViewById<TextView>(R.id.tvDescripcionSAP)
        val umTextView = rowView.findViewById<TextView>(R.id.tvUM)
        val stockTextView = rowView.findViewById<TextView>(R.id.tvStock)
        val requestButton = rowView.findViewById<Button>(R.id.btnRequestItem)

        val item = getItem(position) as InventoryItem

        // Asignar valores a los TextViews
        codigoTextView.text = "Código: ${item.CODIGO}"
        descripcionTextView.text = "Descripción: ${item.DESCRIPCION}"
        codigoSAPTextView.text = "Código SAP: ${item.CodigoSAP}"
        descripcionSAPTextView.text = "Descripción SAP: ${item.DescripcionSAP}"
        umTextView.text = "UM: ${item.UM}"
        stockTextView.text = "Stock: ${item.stock}"

        requestButton.setOnClickListener {
            showRequestDialog(item)
        }

        return rowView
    }

    private fun showRequestDialog(item: InventoryItem) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Solicitar ítem")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Cantidad a solicitar"

        builder.setView(input)

        builder.setPositiveButton("Enviar") { dialog, which ->
            val quantityText = input.text.toString()
            val quantity = quantityText.toIntOrNull()
            if (quantity != null && quantity > 0) {
                sendRequest(item, quantity)
            } else {
                Toast.makeText(context, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun sendRequest(item: InventoryItem, quantity: Int) {
        val request = hashMapOf(
            "CODIGO" to item.CODIGO,
            "DESCRIPCION" to item.DESCRIPCION,
            "CodigoSAP" to item.CodigoSAP,
            "DescripcionSAP" to item.DescripcionSAP,
            "UM" to item.UM,
            "quantity" to quantity,
            "status" to "pending",
            "userId" to auth.currentUser?.uid,
            "userName" to auth.currentUser?.displayName,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("requests").add(request).addOnSuccessListener {
            Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error al enviar solicitud: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }
}
