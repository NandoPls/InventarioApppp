// LoginActivity.kt
package com.example.inventary_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvLoginTitle: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ivLogo = findViewById(R.id.ivLogo)
        tvLoginTitle = findViewById(R.id.tvLoginTitle)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                db.collection("users").document(userId).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")
                        if (role == "admin") {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (role == "trabajador") {
                            val intent = Intent(this, TrabajadorActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Rol de usuario no reconocido", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                        }
                    } else {
                        Toast.makeText(this, "Usuario no registrado en la base de datos", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al obtener datos de usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                    auth.signOut()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al iniciar sesión: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }
}
