// ManageInventoryActivity.kt
package com.example.inventary_app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ManageInventoryActivity : AppCompatActivity() {

    private lateinit var lvInventory: ListView
    private lateinit var btnAddItem: Button
    private val db = FirebaseFirestore.getInstance()
    private val inventoryList = ArrayList<InventoryItem>()
    private lateinit var adapter: InventoryManagementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_inventory)

        lvInventory = findViewById(R.id.lvInventory)
        btnAddItem = findViewById(R.id.btnAddItem)

        adapter = InventoryManagementAdapter(this, inventoryList)
        lvInventory.adapter = adapter

        btnAddItem.setOnClickListener {
            showAddItemDialog()
        }

        loadInventory()
    }

    private fun loadInventory() {
        db.collection("inventory").get().addOnSuccessListener { documents ->
            inventoryList.clear()
            for (document in documents) {
                val item = document.toObject(InventoryItem::class.java)
                item.CODIGO = document.id
                inventoryList.add(item)
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar inventario: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Ítem")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_inventory_item, null)

        val etCodigo = dialogLayout.findViewById<EditText>(R.id.etCodigo)
        val etDescripcion = dialogLayout.findViewById<EditText>(R.id.etDescripcion)
        val etCodigoSAP = dialogLayout.findViewById<EditText>(R.id.etCodigoSAP)
        val etDescripcionSAP = dialogLayout.findViewById<EditText>(R.id.etDescripcionSAP)
        val etUM = dialogLayout.findViewById<EditText>(R.id.etUM)
        val etStock = dialogLayout.findViewById<EditText>(R.id.etStock)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Agregar") { _, _ ->
            val codigo = etCodigo.text.toString()
            val descripcion = etDescripcion.text.toString()
            val codigoSAP = etCodigoSAP.text.toString()
            val descripcionSAP = etDescripcionSAP.text.toString()
            val um = etUM.text.toString()
            val stockText = etStock.text.toString()
            val stock = stockText.toIntOrNull() ?: 0

            if (codigo.isNotEmpty() && descripcion.isNotEmpty()) {
                val item = InventoryItem(
                    CODIGO = codigo,
                    DESCRIPCION = descripcion,
                    CodigoSAP = codigoSAP,
                    DescripcionSAP = descripcionSAP,
                    UM = um,
                    stock = stock
                )
                addItemToInventory(item)
            } else {
                Toast.makeText(this, "Código y Descripción son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun addItemToInventory(item: InventoryItem) {
        db.collection("inventory").document(item.CODIGO).set(item).addOnSuccessListener {
            Toast.makeText(this, "Ítem agregado al inventario", Toast.LENGTH_SHORT).show()
            loadInventory()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al agregar ítem: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun showEditItemDialog(item: InventoryItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Ítem")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_inventory_item, null)

        val etCodigo = dialogLayout.findViewById<EditText>(R.id.etCodigo)
        val etDescripcion = dialogLayout.findViewById<EditText>(R.id.etDescripcion)
        val etCodigoSAP = dialogLayout.findViewById<EditText>(R.id.etCodigoSAP)
        val etDescripcionSAP = dialogLayout.findViewById<EditText>(R.id.etDescripcionSAP)
        val etUM = dialogLayout.findViewById<EditText>(R.id.etUM)
        val etStock = dialogLayout.findViewById<EditText>(R.id.etStock)

        // Prellenar campos
        etCodigo.setText(item.CODIGO)
        etCodigo.isEnabled = false // No permitir cambiar el código
        etDescripcion.setText(item.DESCRIPCION)
        etCodigoSAP.setText(item.CodigoSAP)
        etDescripcionSAP.setText(item.DescripcionSAP)
        etUM.setText(item.UM)
        etStock.setText(item.stock.toString())

        builder.setView(dialogLayout)
        builder.setPositiveButton("Guardar") { _, _ ->
            val descripcion = etDescripcion.text.toString()
            val codigoSAP = etCodigoSAP.text.toString()
            val descripcionSAP = etDescripcionSAP.text.toString()
            val um = etUM.text.toString()
            val stockText = etStock.text.toString()
            val stock = stockText.toIntOrNull() ?: 0

            if (descripcion.isNotEmpty()) {
                val updatedItem = InventoryItem(
                    CODIGO = item.CODIGO,
                    DESCRIPCION = descripcion,
                    CodigoSAP = codigoSAP,
                    DescripcionSAP = descripcionSAP,
                    UM = um,
                    stock = stock
                )
                updateInventoryItem(updatedItem)
            } else {
                Toast.makeText(this, "Descripción es obligatoria", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun updateInventoryItem(item: InventoryItem) {
        db.collection("inventory").document(item.CODIGO).set(item).addOnSuccessListener {
            Toast.makeText(this, "Ítem actualizado", Toast.LENGTH_SHORT).show()
            loadInventory()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al actualizar ítem: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Ítem")
        builder.setMessage("¿Estás seguro de que deseas eliminar el ítem ${item.CODIGO}? Esta acción no se puede deshacer.")
        builder.setPositiveButton("Eliminar") { _, _ ->
            db.collection("inventory").document(item.CODIGO).delete().addOnSuccessListener {
                Toast.makeText(this, "Ítem eliminado", Toast.LENGTH_SHORT).show()
                loadInventory()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar ítem: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
