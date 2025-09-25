package com.rico.shoppinglist.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rico.shoppinglist.R
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview

@Composable
fun FooterSection(
    formattedTotalPrice: String,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(windowInsets)
            .imePadding()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        TotalPriceContainer(formattedTotalPrice)
    }
}

@Composable
private fun TotalPriceContainer(formattedTotalPrice: String) {
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.total),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formattedTotalPrice,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@MultiThemePreview
@Composable
private fun FooterSectionPreview() {
    ShoppingListTheme {
        FooterSection("$123.45")
    }
}