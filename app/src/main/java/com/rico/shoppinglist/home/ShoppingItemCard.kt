package com.rico.shoppinglist.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rico.shoppinglist.data.ShoppingItem
import com.rico.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    modifier: Modifier = Modifier,
    onEvent: (ShoppingEvent) -> Unit = {}
) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 40.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onEvent.invoke(ShoppingEvent.CheckProduct(item)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(24.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                        color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.alpha(if (item.isChecked) 0.5f else 1f)
                    ) {
                        IconButton(
                            onClick = { onEvent.invoke(ShoppingEvent.DecreaseQuantity(item)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease Quantity",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { onEvent.invoke(ShoppingEvent.IncreaseQuantity(item)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Increase Quantity",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    UnitPriceContainer(item, onEvent)
                    Spacer(Modifier.height(4.dp))
                    val subTotal = item.quantity * item.unitPrice
                    Text(
                        text = "$%.2f".format(subTotal),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    when (dismissState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onEvent.invoke(ShoppingEvent.Remove(item))
        }

        SwipeToDismissBoxValue.StartToEnd -> {
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }
}

@Composable
private fun UnitPriceContainer(item: ShoppingItem, onEvent: (ShoppingEvent) -> Unit) {
    var isEditingPrice by remember { mutableStateOf(false) }
    var priceText by remember {
        mutableStateOf(
            TextFieldValue(
                text = item.unitPrice.toString(),
                selection = TextRange(item.unitPrice.toString().length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingPrice) {
        if (isEditingPrice) {
            priceText = priceText.copy(
                selection = TextRange(priceText.text.length)
            )
            focusRequester.requestFocus()
        }
    }

    if (isEditingPrice) {
        TextField(
            modifier = Modifier
                .wrapContentSize()
                .widthIn(max = 100.dp)
                .focusRequester(focusRequester),
            value = priceText,
            onValueChange = { newValue ->
                val sanitized = newValue.text.replace(',', '.')
                if (sanitized.count { it == '.' } <= 1) {
                    priceText = newValue.copy(
                        text = sanitized,
                        selection = TextRange(sanitized.length)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                val newPrice = priceText.text.toDoubleOrNull() ?: 0.0
                onEvent(ShoppingEvent.EditPrice(newPrice, item))
                focusRequester.freeFocus()
                isEditingPrice = false
            }),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    } else {
        Text(
            text = "$%.2f".format(item.unitPrice),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
            color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { isEditingPrice = true }
        )
    }
}

@Preview()
@Composable
fun ShoppingItemCardPreviewUnchecked() {
    ShoppingListTheme(darkTheme = false) {
        val item = ShoppingItem(
            "",
            "Extra Virgin Olive Oil from the Mediterranean with a hint of rosemary and garlic for added flavor",
            1,
            12.99,
            false
        )
        ShoppingItemCard(item)
    }
}