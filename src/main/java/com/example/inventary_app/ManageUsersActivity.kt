// ManageUsersActivity.kt
package com.example.inventary_app

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var lvUsers: ListView
    private lateinit var btnAddUser: Button
    private val db = FirebaseFirestore.getInstance()
    private val usersList = ArrayList<User>()
    private lateinit var adapter: UsersAdapter
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        lvUsers = findViewById(R.id.lvUsers)
        btnAddUser = findViewById(R.id.btnAddUser)

        adapter = UsersAdapter(this, usersList)
        lvUsers.adapter = adapter

        btnAddUser.setOnClickListener {
            showAddUserDialog()
        }

        loadUsers()
    }

    private fun loadUsers() {
        db.collection("users").get().addOnSuccessListener { documents ->
            usersList.clear()
            for (document in documents) {
                val user = document.toObject(User::class.java)
                user.uid = document.id
                usersList.add(user)
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar usuarios: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAddUserDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Usuario")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_user, null)
        val etName = dialogLayout.findViewById<EditText>(R.id.etName)
        val etEmail = dialogLayout.findViewById<EditText>(R.id.etEmail)
        val etPassword = dialogLayout.findViewById<EditText>(R.id.etPassword)
        val spinnerRole = dialogLayout.findViewById<Spinner>(R.id.spinnerRole)

        val roles = arrayOf("admin", "trabajador")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapterSpinner

        builder.setView(dialogLayout)
        builder.setPositiveButton("Crear") { _, _ ->
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val role = spinnerRole.selectedItem.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                createUser(name, email, password, role)
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun createUser(name: String, email: String, password: String, role: String) {
        // Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val uid = authResult.user?.uid
            if (uid != null) {
                // Guardar información adicional en Firestore
                val user = User(uid, name, email, role)
                db.collection("users").document(uid).set(user).addOnSuccessListener {
                    Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                    loadUsers()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al guardar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al crear usuario: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Agregar los siguientes métodos dentro de la clase ManageUsersActivity

    fun showEditUserDialog(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Usuario")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_user, null)
        val etName = dialogLayout.findViewById<EditText>(R.id.etName)
        val etEmail = dialogLayout.findViewById<EditText>(R.id.etEmail)
        val etPassword = dialogLayout.findViewById<EditText>(R.id.etPassword)
        val spinnerRole = dialogLayout.findViewById<Spinner>(R.id.spinnerRole)

        // Prellenar los campos
        etName.setText(user.name)
        etEmail.setText(user.email)
        etEmail.isEnabled = false // No se permite cambiar el correo electrónico
        etPassword.hint = "Dejar en blanco para mantener contraseña"

        val roles = arrayOf("admin", "trabajador")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapterSpinner

        val rolePosition = roles.indexOf(user.role)
        spinnerRole.setSelection(if (rolePosition >= 0) rolePosition else 0)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Guardar") { _, _ ->
            val name = etName.text.toString()
            val password = etPassword.text.toString()
            val role = spinnerRole.selectedItem.toString()

            if (name.isNotEmpty()) {
                updateUser(user, name, password, role)
            } else {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun updateUser(user: User, name: String, password: String, role: String) {
        // Actualizar información en Firestore
        val userUpdates = hashMapOf(
            "name" to name,
            "role" to role
        )
        db.collection("users").document(user.uid).update(userUpdates as Map<String, Any>).addOnSuccessListener {
            if (password.isNotEmpty()) {
                // Actualizar contraseña en Firebase Authentication
                auth.signInWithEmailAndPassword(user.email, password).addOnSuccessListener {
                    auth.currentUser?.updatePassword(password)?.addOnSuccessListener {
                        Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    }?.addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al actualizar contraseña: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al autenticar para actualizar contraseña: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show()
                loadUsers()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al actualizar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteUser(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Usuario")
        builder.setMessage("¿Estás seguro de que deseas eliminar a ${user.name}? Esta acción no se puede deshacer.")
        builder.setPositiveButton("Eliminar") { _, _ ->
            // Eliminar usuario de Firebase Authentication y Firestore
            db.collection("users").document(user.uid).delete().addOnSuccessListener {
                // Necesitas privilegios especiales para eliminar usuarios de Authentication desde el cliente
                // Esto generalmente se hace desde el servidor usando la Admin SDK
                Toast.makeText(this, "Usuario eliminado de Firestore. Es necesario eliminarlo de Authentication manualmente.", Toast.LENGTH_LONG).show()
                loadUsers()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}
