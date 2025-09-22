package com.rico.shoppinglist.data

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ShoppingItemRepositoryImpl @Inject constructor(
    private val dao: ShoppingItemDao
) : ShoppingItemRepository {

    override fun getAllItems(): Flow<List<ShoppingItem>> {
        return dao.getAllItems()
    }

    override suspend fun insertItem(item: ShoppingItem) {
        dao.insertItem(item)
    }

    override suspend fun updateItem(item: ShoppingItem) {
        dao.updateItem(item)
    }

    override suspend fun deleteItem(item: ShoppingItem) {
        dao.deleteItem(item)
    }
}