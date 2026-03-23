package com.rico.shoppinglist.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rico.shoppinglist.R
import com.rico.shoppinglist.addoredit.AddEditBottomSheet
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
    ShoppingListScreen(uiState, onEvent = viewModel::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListScreen(
    uiState: ShoppingListUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (ShoppingEvent) -> Unit = {}
) {
    if (uiState is ShoppingListUiState.Success) {
        HandleSnackbarMessages(uiState, snackbarHostState, onEvent)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = { AddProductButton(onEvent) },
        topBar = { ShoppingTopAppbar() },
        bottomBar = {
            if (uiState is ShoppingListUiState.Success) {
                FooterSection(
                    formattedTotalPrice = stringResource(
                        R.string.price_format, uiState.totalPrice
                    )
                )
            }
        }) { innerPadding ->
        when (uiState) {
            is ShoppingListUiState.Loading -> LoadingContent()
            is ShoppingListUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    EmptyListContent(modifier = Modifier.padding(innerPadding))
                } else {
                    ShoppingListContent(innerPadding, uiState.items, onEvent)
                }
                AddEditBottomSheet(
                    showBottomSheet = uiState.showBottomSheet,
                    selectedProduct = uiState.selectedProduct,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun HandleSnackbarMessages(
    uiState: ShoppingListUiState.Success,
    snackbarHostState: SnackbarHostState,
    onEvent: (ShoppingEvent) -> Unit
) {
    val actionLabel = stringResource(R.string.undo_action)

    uiState.userMessage?.let { messageResId ->
        val snackbarText = stringResource(messageResId)

        LaunchedEffect(messageResId) {
            val result = snackbarHostState.showSnackbar(
                message = snackbarText, actionLabel = actionLabel, duration = SnackbarDuration.Long
            )

            when (result) {
                SnackbarResult.ActionPerformed -> onEvent(ShoppingEvent.UndoDelete)
                SnackbarResult.Dismissed -> onEvent(ShoppingEvent.SnackbarDismissed)
            }
        }
    }
}

@Composable
private fun AddProductButton(onEvent: (ShoppingEvent) -> Unit) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary, onClick = {
            onEvent.invoke(ShoppingEvent.EditIconClicked(ShoppingItem.PLACEHOLDER))
        }) {
        Icon(painter = painterResource(R.drawable.add), contentDescription = "")
    }
}

@Composable
private fun ShoppingListContent(
    innerPadding: PaddingValues, items: List<ShoppingItem>, onEvent: (ShoppingEvent) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(items.size) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(index = 0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            ShoppingItemCard(
                item = item, modifier = Modifier.animateItem(), onEvent = onEvent
            )
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(4) {
            LoadingCard()
        }
    }
}

@Preview
@Composable
private fun LoadingContentPreview() {
    ShoppingListTheme {
        LoadingContent()
    }
}

@MultiThemePreview
@Composable
private fun ShoppingListScreenPreview() {
    ShoppingListTheme {
        val dummyItems = listOf(
            ShoppingItem(name = "Milk", quantity = 2, unitPrice = 1.75, isChecked = true),
            ShoppingItem(
                name = "Organic Bananas", quantity = 1, unitPrice = 2.50, isChecked = false
            ),
            ShoppingItem(
                name = "Whole Wheat Bread", quantity = 1, unitPrice = 3.20, isChecked = false
            ),
            ShoppingItem(
                name = "Extra Virgin Olive Oil", quantity = 1, unitPrice = 12.99, isChecked = false
            )
        )
        val totalPrice = dummyItems.sumOf { it.quantity * it.unitPrice }
        val uiState = ShoppingListUiState.Success(
            items = dummyItems, totalPrice = totalPrice
        )
        ShoppingListScreen(uiState)
    }
}

@Composable
private fun EmptyListContent(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.your_shopping_list_is_empty),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.start_adding_items_to_your_list),
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
        ShoppingListScreen(
            ShoppingListUiState.Success()
        )
    }
}
