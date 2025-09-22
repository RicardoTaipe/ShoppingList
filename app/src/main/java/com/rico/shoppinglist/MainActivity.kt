package com.rico.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.home.HomeViewModel
import com.rico.shoppinglist.home.ShoppingItemCard
import com.rico.shoppinglist.home.ShoppingListUiState
import com.rico.shoppinglist.home.components.FooterSection
import com.rico.shoppinglist.home.components.ShoppingTopAppbar
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                ShoppingListScreen()
            }
        }
    }
}

@Composable
fun ShoppingListScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ShoppingListScreen(uiState)
}

@Composable
fun ShoppingListScreen(uiState: ShoppingListUiState) {
    val formattedTotalPrice = "$%.2f".format(uiState.totalPrice)
    val items = uiState.items
    Scaffold(
        topBar = { ShoppingTopAppbar() },
        bottomBar = { FooterSection(formattedTotalPrice) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = items, key = { it.id }) { item ->
                ShoppingItemCard(item = item)
            }
        }
    }
}

@MultiThemePreview
@Composable
fun AppPreview() {
    ShoppingListTheme {
        val dummyItems = listOf(
            ShoppingItem(Random.nextInt(), "Milk", 2, 1.75, true),
            ShoppingItem(Random.nextInt(), "Organic Bananas", 1, 2.50, false),
            ShoppingItem(Random.nextInt(), "Whole Wheat Bread", 1, 3.20, false),
            ShoppingItem(Random.nextInt(), "Extra Virgin Olive Oil", 1, 12.99, false)
        )
        val totalPrice = dummyItems.sumOf { it.quantity * it.unitPrice }
        val uiState = ShoppingListUiState(
            items = dummyItems,
            totalPrice = totalPrice
        )
        ShoppingListScreen(uiState)
    }
}