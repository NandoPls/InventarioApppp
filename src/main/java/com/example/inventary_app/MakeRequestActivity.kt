// MakeRequestActivity.kt
package com.example.inventary_app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MakeRequestActivity : AppCompatActivity() {

    private lateinit var rvInventory: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val inventoryList = ArrayList<InventoryItem>()
    private lateinit var adapter: MakeRequestAdapter
    private val selectedItems = HashMap<String, Int>() // Código del ítem y cantidad solicitada
    private val auth = FirebaseAuth.getInstance()
    private var userName: String = "Trabajador"
    private lateinit var btnSendRequest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_request)

        rvInventory = findViewById(R.id.rvInventory)
        btnSendRequest = findViewById(R.id.btnSendRequest)

        adapter = MakeRequestAdapter(this, inventoryList, selectedItems)
        rvInventory.adapter = adapter
        rvInventory.layoutManager = LinearLayoutManager(this)

        btnSendRequest.setOnClickListener {
            sendRequest()
        }

        loadInventory()

        // Obtener el nombre del usuario
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.getString("name") ?: "Trabajador"
                }
            }
        }
    }

    private fun loadInventory() {
        db.collection("inventory").get().addOnSuccessListener { documents ->
            inventoryList.clear()
            for (document in documents) {
                val item = document.toObject(InventoryItem::class.java)
                inventoryList.add(item)
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar el inventario: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendRequest() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No has seleccionado ningún ítem", Toast.LENGTH_SHORT).show()
            return
        }

        val request = hashMapOf(
            "items" to selectedItems,
            "status" to "pending",
            "userId" to auth.currentUser?.uid,
            "userName" to userName,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        db.collection("requests").add(request).addOnSuccessListener {
            Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show()
            // Restar del stock
            for ((codigo, cantidad) in selectedItems) {
                db.collection("inventory").document(codigo).update("stock", com.google.firebase.firestore.FieldValue.increment(-cantidad.toLong()))
            }
            selectedItems.clear()
            adapter.notifyDataSetChanged()
            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al enviar solicitud: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }
}
