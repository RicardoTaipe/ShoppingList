package com.rico.shoppinglist.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.home.components.FooterSection
import com.rico.shoppinglist.home.components.ShoppingTopAppbar
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview

@Composable
fun ShoppingListRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ShoppingListScreen(uiState) {
        viewModel.onEvent(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(uiState: ShoppingListUiState, onEvent: (ShoppingEvent) -> Unit = {}) {
    val formattedTotalPrice = "$%.2f".format(uiState.totalPrice)
    val items = uiState.items
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { ShoppingTopAppbar(scrollBehavior) },
        bottomBar = { FooterSection(formattedTotalPrice, onEvent = onEvent) }
    ) { innerPadding ->
        if (items.isEmpty()) {
            EmptyListContent()
        } else {
            ShoppingListContent(innerPadding, items, onEvent)
        }
    }
}

@Composable
private fun ShoppingListContent(
    innerPadding: PaddingValues,
    items: List<ShoppingItem>,
    onEvent: (ShoppingEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            ShoppingItemCard(
                item = item,
                modifier = Modifier.animateItem(),
                onEvent = onEvent
            )
        }
    }
}

@MultiThemePreview
@Composable
private fun ShoppingListScreenPreview() {
    ShoppingListTheme {
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
        val totalPrice = dummyItems.sumOf { it.quantity * it.unitPrice }
        val uiState = ShoppingListUiState(
            items = dummyItems, totalPrice = totalPrice
        )
        ShoppingListScreen(uiState)
    }
}

@Composable
private fun EmptyListContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Your shopping list is empty.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Start adding items to your list!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@MultiThemePreview
@Composable
private fun EmptyListContentPreview() {
    ShoppingListTheme {
        ShoppingListScreen(ShoppingListUiState())
    }
}