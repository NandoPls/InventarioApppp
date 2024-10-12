// UsersAdapter.kt
package com.example.inventary_app

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsersAdapter(private val context: Context, private val dataSource: ArrayList<User>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun getCount(): Int = dataSource.size

    override fun getItem(position: Int): Any = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_user, parent, false)

        val tvName = rowView.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = rowView.findViewById<TextView>(R.id.tvUserEmail)
        val tvRole = rowView.findViewById<TextView>(R.id.tvUserRole)
        val btnEdit = rowView.findViewById<Button>(R.id.btnEditUser)
        val btnDelete = rowView.findViewById<Button>(R.id.btnDeleteUser)

        val user = getItem(position) as User

        tvName.text = user.name
        tvEmail.text = user.email
        tvRole.text = "Rol: ${user.role}"

        btnEdit.setOnClickListener {
            (context as ManageUsersActivity).showEditUserDialog(user)
        }

        btnDelete.setOnClickListener {
            (context as ManageUsersActivity).deleteUser(user)
        }

        return rowView
    }
}
