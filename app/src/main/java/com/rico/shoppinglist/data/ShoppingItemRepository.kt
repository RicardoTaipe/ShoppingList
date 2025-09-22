package com.rico.shoppinglist.data

import kotlinx.coroutines.flow.Flow

interface ShoppingItemRepository {
    fun getAllItems(): Flow<List<ShoppingItem>>
    suspend fun insertItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(item: ShoppingItem)
}