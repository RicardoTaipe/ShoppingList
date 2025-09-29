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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rico.shoppinglist.R
import com.rico.shoppinglist.home.ShoppingEvent
import com.rico.shoppinglist.home.components.TextField
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onEvent: (ShoppingEvent) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    if (!showBottomSheet) return
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(
            color = MaterialTheme.colorScheme.surfaceVariant
        ) },
        onDismissRequest = { onEvent.invoke(ShoppingEvent.ModalDismissed) },
        sheetState = sheetState
    ) {
        var name by remember { mutableStateOf("Organic Bananas") }
        var price by remember { mutableStateOf("2.50") }

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
                label = stringResource(R.string.price),
                value = price,
                prefix = stringResource(R.string.dollar_sign),
                onValueChange = { price = it },
                placeholder = stringResource(R.string.price_number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cancel Button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onEvent.invoke(ShoppingEvent.ModalDismissed) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp) // Match rounded-lg
                ) {
                    Text(stringResource(R.string.cancel), fontWeight = FontWeight.Bold)
                }

                // Edit/Save Button
                Button(
                    onClick = { /* Handle Edit/Save Logic */ },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp) // Match rounded-lg
                ) {
                    Text("Edit", fontWeight = FontWeight.Bold)
                }
            }
        }

//        Button(onClick = {
//            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                if (!sheetState.isVisible) {
//                    onDismissRequest
//                }
//            }
//        }) {
//            Text("Hide bottom sheet")
//        }
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
        AddEditBottomSheet(sheetState = sheetState)
    }
}