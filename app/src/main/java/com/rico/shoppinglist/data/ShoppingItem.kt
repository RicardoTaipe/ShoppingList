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
        val PLACEHOLDER = ShoppingItem(
            id = "",
            name = "",
            quantity = 0,
            unitPrice = 0.0,
            isChecked = false,
            timestamp = 0L
        )
    }
    fun isExistingProduct() = this.id.isNotEmpty()

    fun copyWithoutId(): ShoppingItem {
        // We call copy() and explicitly set the 'id' to its default value.
        // The default value is UUID.randomUUID().toString().
        return this.copy(id = UUID.randomUUID().toString())
    }

}