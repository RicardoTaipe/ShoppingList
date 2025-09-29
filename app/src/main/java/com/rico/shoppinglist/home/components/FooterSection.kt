package com.rico.shoppinglist.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rico.shoppinglist.R
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview

@Composable
fun FooterSection(
    modifier: Modifier = Modifier,
    formattedTotalPrice: String,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    Surface(
        shadowElevation = 8.dp,
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .shadow(elevation = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TotalPriceContainer(formattedTotalPrice)
    }
}

@Composable
private fun TotalPriceContainer(formattedTotalPrice: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
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
        FooterSection(formattedTotalPrice = "$123.45")
    }
}