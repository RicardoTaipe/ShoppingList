package com.rico.shoppinglist.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rico.shoppinglist.R
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
    data class AddNewItem(val productName: String, val price: String) : ShoppingEvent()
    data class Remove(val item: ShoppingItem) : ShoppingEvent()

    data object UndoDelete : ShoppingEvent()
    data object SnackbarDismissed : ShoppingEvent()
}

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val showBottomSheet: Boolean = false,
    val selectedProduct: ShoppingItem = ShoppingItem.PLACEHOLDER,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ShoppingItemRepository) :
    ViewModel() {
    private var recentlyDeletedItem: ShoppingItem? = null

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val selectedProduct = MutableStateFlow(ShoppingItem.PLACEHOLDER)
    private val showBottomSheet = MutableStateFlow(false)

    val uiState: StateFlow<ShoppingListUiState> =
        combine(
            repository.getAllItems(),
            showBottomSheet,
            selectedProduct,
            _userMessage
        ) { items, showBottomSheet, selectedProduct, userMessage ->
            ShoppingListUiState(
                items = items,
                totalPrice = calculateTotalPrice(items),
                showBottomSheet = showBottomSheet,
                selectedProduct = selectedProduct,
                userMessage = userMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ShoppingListUiState()
        )


    @SuppressLint("DefaultLocale")
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
                        unitPrice = event.price.toDouble().let { String.format("%.2f", it).toDouble() },
                        isChecked = false
                    )
                    showBottomSheet.value = false
                    repository.insertItem(newItem)
                }

                is ShoppingEvent.Remove -> {
                    recentlyDeletedItem = event.item
                    repository.deleteItem(event.item)
                    _userMessage.value = R.string.undo_message
                }

                ShoppingEvent.UndoDelete -> {
                    recentlyDeletedItem?.let { item ->
                        repository.insertItem(item.copyWithoutId())
                    }
                    recentlyDeletedItem = null
                    snackbarMessageShown()
                }

                is ShoppingEvent.EditIconClicked -> {
                    selectedProduct.value = event.item
                    showBottomSheet.value = true
                }

                ShoppingEvent.ModalDismissed -> showBottomSheet.value = false
                ShoppingEvent.SnackbarDismissed -> {
                    snackbarMessageShown()
                }
            }
        }
    }

    private fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun calculateTotalPrice(items: List<ShoppingItem>): Double {
        return items.sumOf { it.quantity * it.unitPrice }
    }
}