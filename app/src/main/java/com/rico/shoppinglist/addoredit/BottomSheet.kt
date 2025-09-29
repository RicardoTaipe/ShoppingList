package com.rico.shoppinglist.addoredit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rico.shoppinglist.R
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.home.ShoppingEvent
import com.rico.shoppinglist.home.components.TextField
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean = true,
    selectedProduct: ShoppingItem = ShoppingItem.PLACEHOLDER,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onEvent: (ShoppingEvent) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    if (!showBottomSheet) return
    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        onDismissRequest = { onEvent.invoke(ShoppingEvent.ModalDismissed) },
        sheetState = sheetState
    ) {
        var name by rememberSaveable(selectedProduct.name) {
            mutableStateOf(
                selectedProduct.name.ifEmpty { "" }
            )
        }

        var unitPrice by rememberSaveable(selectedProduct.unitPrice) {
            mutableStateOf(
                if (selectedProduct.unitPrice > 0.00) {
                    "%.2f".format(selectedProduct.unitPrice)
                } else {
                    ""
                }
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                label = stringResource(R.string.product_name),
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.e_g_milk)
            )


            TextField(
                isNumber = true,
                label = stringResource(R.string.price),
                value = unitPrice,
                prefix = stringResource(R.string.dollar_sign),
                onValueChange = { unitPrice = it },
                placeholder = stringResource(R.string.price_number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onEvent.invoke(ShoppingEvent.ModalDismissed) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        if (selectedProduct.isExistingProduct()) {
                            onEvent.invoke(
                                ShoppingEvent.EditProduct(
                                    newPrice = unitPrice.replace(",", "."),
                                    newName = name,
                                    item = selectedProduct
                                )
                            )
                        } else {
                            onEvent.invoke(
                                ShoppingEvent.AddNewItem(
                                    productName = name
                                )
                            )
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onEvent.invoke(ShoppingEvent.ModalDismissed)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@MultiThemePreview
@Composable
private fun AddEditBottomSheetPreview() {
    // use rememberStandardBottomSheetState instead of rememberModalBottomSheetState
    // for preview
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = false
    )
    ShoppingListTheme {
        val selectedProduct = ShoppingItem(
            id = "1",
            name = "Milk",
            quantity = 1,
            unitPrice = 0.0,
            isChecked = false
        )
        AddEditBottomSheet(sheetState = sheetState, selectedProduct = selectedProduct)
    }
}