// ListInventoryActivity.kt
package com.example.inventary_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

class ListInventoryActivity : AppCompatActivity(), InventoryRecyclerAdapter.OnItemClickListener {

    private lateinit var rvInventory: RecyclerView
    private lateinit var searchView: SearchView
    private val db = FirebaseFirestore.getInstance()
    private val inventoryList = ArrayList<InventoryItem>()
    private lateinit var adapter: InventoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_inventory)

        rvInventory = findViewById(R.id.rvInventory)
        searchView = findViewById(R.id.searchViewInventory)

        adapter = InventoryRecyclerAdapter(this, inventoryList, this)
        rvInventory.adapter = adapter
        rvInventory.layoutManager = LinearLayoutManager(this)

        loadInventory()

        // Configurar el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // No es necesario manejar el submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun loadInventory() {
        db.collection("inventory").get().addOnSuccessListener { documents ->
            inventoryList.clear()
            for (document in documents) {
                val item = document.toObject(InventoryItem::class.java)
                inventoryList.add(item)
            }
            adapter.notifyDataSetChanged()
            adapter.inventoryListFull = ArrayList(inventoryList) // Actualizar lista completa para el filtro
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar el inventario: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(item: InventoryItem) {
        // Manejar el clic en un ítem
        // Por ejemplo, mostrar detalles o permitir realizar una solicitud
        Toast.makeText(this, "Seleccionaste: ${item.CODIGO}", Toast.LENGTH_SHORT).show()
    }
}
