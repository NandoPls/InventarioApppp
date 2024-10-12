// ListRequestsActivity.kt
package com.example.inventary_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ListRequestsActivity : AppCompatActivity() {

    private lateinit var rvRequests: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val requestsList = ArrayList<RequestItem>()
    private lateinit var adapter: AdminRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_requests)

        rvRequests = findViewById(R.id.rvRequests)

        adapter = AdminRequestsAdapter(this, requestsList)
        rvRequests.adapter = adapter
        rvRequests.layoutManager = LinearLayoutManager(this)

        loadRequests()
    }

    private fun loadRequests() {
        db.collection("requests")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { documents ->
                requestsList.clear()
                for (document in documents) {
                    val request = document.toObject(RequestItem::class.java)
                    request.requestId = document.id
                    requestsList.add(request)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar solicitudes: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
