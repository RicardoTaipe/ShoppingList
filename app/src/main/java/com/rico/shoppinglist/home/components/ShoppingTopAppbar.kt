package com.rico.shoppinglist.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rico.shoppinglist.R
import com.rico.shoppinglist.ui.theme.ShoppingListTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ShoppingTopAppbar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(R.string.shopping_list),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview
@Composable
private fun ShoppingTopAppbarPreview() {
    ShoppingListTheme {
        ShoppingTopAppbar()
    }
}