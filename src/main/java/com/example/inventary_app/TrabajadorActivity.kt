// TrabajadorActivity.kt
package com.example.inventary_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TrabajadorActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnViewInventory: Button
    private lateinit var btnMakeRequest: Button
    private lateinit var btnViewRequests: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    private var userName: String = "Trabajador"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trabajador)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        btnViewInventory = findViewById(R.id.btnViewInventory)
        btnMakeRequest = findViewById(R.id.btnMakeRequest)
        btnViewRequests = findViewById(R.id.btnViewRequests)

        // Obtener el nombre del usuario actual
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.getString("name") ?: "Trabajador"
                    tvWelcome.text = "Bienvenido, $userName"
                }
            }.addOnFailureListener {
                tvWelcome.text = "Bienvenido"
            }
        } else {
            tvWelcome.text = "Bienvenido"
        }

        // Configurar listeners
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnViewInventory.setOnClickListener {
            val intent = Intent(this, ViewInventoryActivity::class.java)
            startActivity(intent)
        }

        btnMakeRequest.setOnClickListener {
            val intent = Intent(this, MakeRequestActivity::class.java)
            startActivity(intent)
        }

        btnViewRequests.setOnClickListener {
            val intent = Intent(this, WorkerRequestsActivity::class.java)
            startActivity(intent)
        }
    }
}
