package com.rico.shoppinglist.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.data.ShoppingItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextLong

sealed class ShoppingEvent {
    data class CheckProduct(val item: ShoppingItem) : ShoppingEvent()
    data class IncreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class DecreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class EditPrice(val newPrice: Double, val item: ShoppingItem) : ShoppingEvent()
    data class AddNewItem(val productName: String) : ShoppingEvent()
    data class Remove(val item: ShoppingItem) : ShoppingEvent()
}

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val totalPrice: Double = 0.0
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ShoppingItemRepository) : ViewModel() {

    val uiState: StateFlow<ShoppingListUiState> = repository.getAllItems()
        .map { items ->
            // Whenever the items from the database change, we create a new UI state.
            ShoppingListUiState(
                items = items,
                totalPrice = calculateTotalPrice(items)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ShoppingListUiState()
        )

    fun onEvent(event: ShoppingEvent) {
        viewModelScope.launch {
            when (event) {
                is ShoppingEvent.CheckProduct -> {
                    val updatedItem = event.item.copy(isChecked = !event.item.isChecked)
                    repository.updateItem(updatedItem)
                }
                is ShoppingEvent.DecreaseQuantity -> {
                    val updatedItem = event.item.copy(quantity = (event.item.quantity - 1).coerceAtLeast(1))
                    repository.updateItem(updatedItem)
                }
                is ShoppingEvent.IncreaseQuantity -> {
                    val updatedItem = event.item.copy(quantity = event.item.quantity + 1)
                    repository.updateItem(updatedItem)
                }
                is ShoppingEvent.EditPrice -> {
                    val updatedItem = event.item.copy(unitPrice = event.newPrice)
                    repository.updateItem(updatedItem)
                }
                is ShoppingEvent.AddNewItem -> {
                    val newItem = ShoppingItem(
                        name = event.productName,
                        quantity = 1,
                        unitPrice = 0.0,
                        isChecked = false
                    )
                    repository.insertItem(newItem)
                }
                is ShoppingEvent.Remove -> {
                    repository.deleteItem(event.item)
                }
            }
        }
    }

    // A private helper function to calculate the total
    private fun calculateTotalPrice(items: List<ShoppingItem>): Double {
        return items.sumOf { it.quantity * it.unitPrice }
    }
}