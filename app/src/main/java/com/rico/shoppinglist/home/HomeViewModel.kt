package com.rico.shoppinglist.home

import androidx.lifecycle.ViewModel
import com.rico.shoppinglist.data.ShoppingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

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

        _uiState.value = _uiState.value.copy(
            items = dummyItems,
            totalPrice = dummyItems.sumOf { if (!it.isChecked) it.quantity * it.unitPrice else 0.0 }
        )
    }
}