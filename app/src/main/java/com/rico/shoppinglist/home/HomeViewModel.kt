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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ShoppingEvent {
    data class CheckProduct(val item: ShoppingItem) : ShoppingEvent()
    data class IncreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class DecreaseQuantity(val item: ShoppingItem) : ShoppingEvent()
    data class EditProduct(val newPrice: String, val newName: String, val item: ShoppingItem) :
        ShoppingEvent()

    data class EditIconClicked(val item: ShoppingItem) : ShoppingEvent()

    data object ModalDismissed : ShoppingEvent()
    data class AddNewItem(val productName: String) : ShoppingEvent()
    data class Remove(val item: ShoppingItem) : ShoppingEvent()
}

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val showBottomSheet: Boolean = false,
    val selectedProduct: ShoppingItem = ShoppingItem.PLACEHOLDER
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ShoppingItemRepository) :
    ViewModel() {

    private val selectedProduct = MutableStateFlow(ShoppingItem.PLACEHOLDER)
    private val showBottomSheet = MutableStateFlow(false)
    val uiState: StateFlow<ShoppingListUiState> =
        combine(
            repository.getAllItems(),
            showBottomSheet,
            selectedProduct
        ) { items, showBottomSheet, selectedProduct ->
            ShoppingListUiState(
                items = items,
                totalPrice = calculateTotalPrice(items),
                showBottomSheet = showBottomSheet,
                selectedProduct = selectedProduct
            )
        }.stateIn(
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
                    val updatedItem =
                        event.item.copy(quantity = (event.item.quantity - 1).coerceAtLeast(1))
                    repository.updateItem(updatedItem)
                }

                is ShoppingEvent.IncreaseQuantity -> {
                    val updatedItem = event.item.copy(quantity = event.item.quantity + 1)
                    repository.updateItem(updatedItem)
                }

                is ShoppingEvent.EditProduct -> {
                    val newPrice =
                        event.newPrice.takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0
                    val updatedItem = event.item.copy(unitPrice = newPrice, name = event.newName)
                    repository.updateItem(updatedItem)
                    showBottomSheet.value = false
                }

                is ShoppingEvent.AddNewItem -> {
                    val newItem = ShoppingItem(
                        name = event.productName,
                        quantity = 1,
                        unitPrice = 0.0,
                        isChecked = false
                    )
                    showBottomSheet.value = false
                    repository.insertItem(newItem)
                }

                is ShoppingEvent.Remove -> {
                    repository.deleteItem(event.item)
                }

                is ShoppingEvent.EditIconClicked -> {
                    selectedProduct.value = event.item
                    showBottomSheet.value = true
                }

                ShoppingEvent.ModalDismissed -> showBottomSheet.value = false
            }
        }
    }

    private fun calculateTotalPrice(items: List<ShoppingItem>): Double {
        return items.sumOf { it.quantity * it.unitPrice }
    }
}