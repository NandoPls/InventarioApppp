// InventoryItem.kt
package com.example.inventary_app

data class InventoryItem(
    var CODIGO: String = "",
    var DESCRIPCION: String = "",
    var CodigoSAP: String = "",
    var DescripcionSAP: String = "",
    var UM: String = "",
    var stock: Int = 0
)
