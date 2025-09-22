package com.rico.shoppinglist.home

import androidx.lifecycle.ViewModel
import com.rico.shoppinglist.data.ShoppingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

sealed class ShoppingEvent {
    data class CheckProduct(val item: ShoppingItem) : ShoppingEvent()
    data class IncreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class DecreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class EditPrice(val price: Double, val item: ShoppingItem) : ShoppingEvent()
}

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val totalPrice: Double = 0.0,
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState = _uiState.asStateFlow()

    init {

        val dummyItems = listOf(
            ShoppingItem(Random.nextInt(), "Milk", 2, 1.75, true),
            ShoppingItem(Random.nextInt(), "Organic Bananas", 1, 2.50, false),
            ShoppingItem(Random.nextInt(), "Whole Wheat Bread", 1, 3.20, false),
            ShoppingItem(Random.nextInt(), "Extra Virgin Olive Oil", 1, 12.99, false)
        )

        _uiState.update { currentState ->
            currentState.copy(
                items = dummyItems,
                totalPrice = dummyItems.sumOf { it.quantity * it.unitPrice }
            )
        }
    }

    fun onEvent(event: ShoppingEvent) {
        when (event) {
            is ShoppingEvent.CheckProduct -> {
                updateItem(event.item)
            }
            is ShoppingEvent.DecreaseQuantity -> {}
            is ShoppingEvent.IncreaseQuantity -> {}
            is ShoppingEvent.EditPrice -> {}
        }
    }

    private fun updateItem(item: ShoppingItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == item.id) item.copy(isChecked = !it.isChecked) else it
            }
            currentState.copy(
                items = updatedItems,
                totalPrice = updatedItems.sumOf { it.quantity * it.unitPrice }
            )
        }
    }
}