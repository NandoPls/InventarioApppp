// ViewInventoryActivity.kt
package com.example.inventary_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ViewInventoryActivity : AppCompatActivity() {

    private lateinit var rvInventory: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val inventoryList = ArrayList<InventoryItem>()
    private lateinit var adapter: InventoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_inventory)

        rvInventory = findViewById(R.id.rvInventory)

        adapter = InventoryRecyclerAdapter(this, inventoryList, object : InventoryRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: InventoryItem) {
                // Puedes manejar el clic en un ítem aquí si lo deseas
            }
        })
        rvInventory.adapter = adapter
        rvInventory.layoutManager = LinearLayoutManager(this)

        loadInventory()
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
}
