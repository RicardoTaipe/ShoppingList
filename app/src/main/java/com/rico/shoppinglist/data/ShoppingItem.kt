package com.rico.shoppinglist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey()
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val isChecked: Boolean = false,
    val timestamp: Long = Date().time
)