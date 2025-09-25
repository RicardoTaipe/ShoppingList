package com.rico.shoppinglist.addoredit

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.rico.shoppinglist.ui.theme.ShoppingListTheme
import com.rico.shoppinglist.utils.MultiThemePreview
import kotlinx.coroutines.launch

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
        Button(onClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest
                }
            }
        }) {
            Text("Hide bottom sheet")
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
        AddEditBottomSheet(sheetState = sheetState)
    }
}