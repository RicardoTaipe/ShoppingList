package com.rico.shoppinglist.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.data.ShoppingItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class HomeViewModel @Inject constructor(private val repository: ShoppingItemRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val dummyItems = listOf(
            ShoppingItem(name = "Milk", quantity = 2, unitPrice = 1.75, isChecked = true),
            ShoppingItem(
                name = "Organic Bananas",
                quantity = 1,
                unitPrice = 2.50,
                isChecked = false
            ),
            ShoppingItem(
                name = "Whole Wheat Bread",
                quantity = 1,
                unitPrice = 3.20,
                isChecked = false
            ),
            ShoppingItem(
                name = "Extra Virgin Olive Oil",
                quantity = 1,
                unitPrice = 12.99,
                isChecked = false
            )
        )
        _uiState.update { currentState ->
            updateState(items = dummyItems)
        }
    }

    fun onEvent(event: ShoppingEvent) {
        when (event) {
            is ShoppingEvent.CheckProduct -> {
                toggleItemChecked(event.item)
            }

            is ShoppingEvent.DecreaseQuantity -> {
                decreaseItemQuantity(event.item)
            }

            is ShoppingEvent.IncreaseQuantity -> {
                increaseItemQuantity(event.item)
            }

            is ShoppingEvent.EditPrice -> {
                editItemPrice(event.item, event.newPrice)
            }

            is ShoppingEvent.AddNewItem -> {
                addItem(event.productName)
            }

            is ShoppingEvent.Remove -> {
                _uiState.update { currentState ->
                    val updatedItems = currentState.items.filter { it.id != event.item.id }
                    updateState(items = updatedItems)
                }
                viewModelScope.launch {
                    repository.deleteItem(item = event.item)
                }
            }
        }
    }

    private fun addItem(productName: String) {
        val newItem = ShoppingItem(
            name = productName,
            quantity = 1,
            unitPrice = 0.0,
            isChecked = false
        )
        _uiState.update { currentState ->
            val updatedItems = currentState.items + newItem // Add the new item to the list
            updateState(items = updatedItems)
        }
        viewModelScope.launch {
            repository.insertItem(newItem)
        }
    }

    private fun toggleItemChecked(item: ShoppingItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it
            }
            updateState(items = updatedItems)
        }
    }

    private fun decreaseItemQuantity(item: ShoppingItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == item.id) {
                    // Decrease quantity, but ensure it's not less than 1
                    it.copy(quantity = (it.quantity - 1).coerceAtLeast(1))
                } else {
                    it
                }
            }
            updateState(items = updatedItems)
        }
    }

    private fun increaseItemQuantity(item: ShoppingItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
            }
            updateState(items = updatedItems)
        }
    }

    private fun editItemPrice(item: ShoppingItem, newPrice: Double) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == item.id) it.copy(unitPrice = newPrice) else it
            }
            updateState(items = updatedItems)
        }
    }

    // A private helper function to update the state safely.
    private fun updateState(items: List<ShoppingItem>): ShoppingListUiState {
        val newTotalPrice = calculateTotalPrice(items)
        return ShoppingListUiState(
            items = items,
            totalPrice = newTotalPrice
        )
    }

    // A private helper function to calculate the total of items.
    private fun calculateTotalPrice(items: List<ShoppingItem>): Double {
        return items.sumOf { it.quantity * it.unitPrice }
    }
}