// RequestItem.kt
package com.example.inventary_app

import com.google.firebase.Timestamp

data class RequestItem(
    var requestId: String = "",
    var itemId: String = "",
    var itemName: String = "",
    var quantity: Int = 0,
    var items: Map<String, Int>? = null, // Agrega esta línea
    var status: String = "",
    var userId: String = "",
    var userName: String = "",
    var timestamp: Timestamp? = null
)
