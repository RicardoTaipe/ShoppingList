package com.rico.shoppinglist.addoredit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    if (!showBottomSheet) return
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        var name by remember { mutableStateOf("Organic Bananas") }
        var price by remember { mutableStateOf("2.50") }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Product Name Field
            TextFieldWithLabel(
                label = "Product Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "e.g., Milk"
            )

            // Price Field
            PriceTextFieldWithLabel(
                label = "Price",
                value = price,
                onValueChange = { price = it },
                placeholder = "0.00"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // --- Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cancel Button
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp) // Match rounded-lg
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }

            // Edit/Save Button
            Button(
                onClick = { /* Handle Edit/Save Logic */ onDismissRequest() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp) // Match rounded-lg
            ) {
                Text("Edit", fontWeight = FontWeight.Bold)
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


// --- Helper Composable for Standard TextField ---

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            //color = MutedForegroundDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = BackgroundDark,
//                unfocusedContainerColor = BackgroundDark,
//                focusedBorderColor = PrimaryGreen,
//                unfocusedBorderColor = BorderDark,
//                focusedTextColor = ForegroundDark,
//                unfocusedTextColor = ForegroundDark
//            ),
//            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
//                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
//            )
        )
    }
}

// --- Helper Composable for Price TextField with $ Prefix ---

@Composable
fun PriceTextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            // Add a leading $ icon/text
            leadingIcon = {
                Text(
                    text = "$",
                    modifier = Modifier.padding(start = 4.dp)
                )
            },
//            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
//                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
//            )
        )
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