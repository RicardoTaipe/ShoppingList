package com.rico.shoppinglist.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Immutable
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey()
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val isChecked: Boolean = false,
    val timestamp: Long = Date().time
) {
    companion object {
        // Define a constant property that holds the "empty" instance
        val EMPTY_ITEM = ShoppingItem(id = "", name = "", quantity = 1, unitPrice = 0.0)
    }
}