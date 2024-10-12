// AdminActivity.kt
package com.example.inventary_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnManageUsers: Button
    private lateinit var btnViewRequests: Button
    private lateinit var btnManageInventory: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var userName: String = "Administrador"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        btnManageUsers = findViewById(R.id.btnManageUsers)
        btnViewRequests = findViewById(R.id.btnViewRequests)
        btnManageInventory = findViewById(R.id.btnManageInventory)

        // Obtener el nombre del usuario actual
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.getString("name") ?: "Administrador"
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
            startActivity(intent)
            finish()
        }

        btnManageUsers.setOnClickListener {
            val intent = Intent(this, ManageUsersActivity::class.java)
            startActivity(intent)
        }

        btnViewRequests.setOnClickListener {
            val intent = Intent(this, ListRequestsActivity::class.java)
            startActivity(intent)
        }

        btnManageInventory.setOnClickListener {
            val intent = Intent(this, ManageInventoryActivity::class.java)
            startActivity(intent)
        }
    }
}
