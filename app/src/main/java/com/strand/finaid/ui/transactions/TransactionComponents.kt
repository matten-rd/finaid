package com.strand.finaid.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strand.finaid.R
import com.strand.finaid.ext.formatDayMonthYear
import com.strand.finaid.ext.toLocalDate
import com.strand.finaid.ext.toMillis
import com.strand.finaid.ui.components.BaseBottomSheet
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.textfield.FinaidTextField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    ReadonlyTextField(
        modifier = modifier,
        value = currentDate.formatDayMonthYear(),
        label = stringResource(id = R.string.select_date),
        leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) }
    ) {
        openDialog.value = true
    }

    if (openDialog.value) {
        FinaidDatePicker(initialDate = currentDate, onDateChange = onDateChange) {
            openDialog.value = false
        }
    }
}

@Composable
fun ReadonlyTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "",
    supportingText: String = "",
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Box {
        FinaidTextField(
            readOnly = true,
            modifier = modifier,
            value = value,
            onValueChange = {},
            label = label,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = colors
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable { onClick() }
        )
    }
}

@Composable
fun FinaidDatePicker(
    initialDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    closeDialog: () -> Unit
) {
    val initialDateMillis = initialDate.toMillis()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = closeDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateChange(datePickerState.selectedDateMillis!!.toLocalDate())
                    closeDialog()
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = closeDialog) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@Composable
fun TransactionsSortBottomSheet(
    onClose: () -> Unit,
    possibleSortOrders: List<Int>,
    selectedSortOrder: SortOrder,
    onSelectedSortOrder: (Int) -> Unit
) {
    var selectedSortOrderIndex by remember(selectedSortOrder) { mutableStateOf(selectedSortOrder.ordinal) }

    BaseBottomSheet(title = stringResource(id = R.string.sort_by)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SegmentedButton(
                items = possibleSortOrders.map { id -> stringResource(id = id) },
                selectedIndex = selectedSortOrderIndex,
                indexChanged = { newIndex -> selectedSortOrderIndex = newIndex }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        onClose()
                        onSelectedSortOrder(selectedSortOrderIndex)
                    }
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }

}