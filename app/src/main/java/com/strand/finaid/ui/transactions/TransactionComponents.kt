package com.strand.finaid.ui.transactions

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strand.finaid.R
import com.strand.finaid.ui.components.BaseBottomSheet
import com.strand.finaid.ui.components.textfield.FinaidTextField
import com.strand.finaid.ui.components.SegmentedButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val datePicker = rememberDatePicker(currentDate = currentDate, onDateChange = onDateChange)

    ReadonlyTextField(
        modifier = modifier,
        value = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        label = "Välj datum",
        leadingIcon = { Icon(imageVector = Icons.Rounded.DateRange, contentDescription = null) }
    ) {
        datePicker.show()
    }
}

@Composable
fun ReadonlyTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "",
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
private fun rememberDatePicker(currentDate: LocalDate, onDateChange: (LocalDate) -> Unit): DatePickerDialog {
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerDialogTheme,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            onDateChange(LocalDate.of(year, month+1, dayOfMonth))
        },
        currentDate.year, currentDate.monthValue-1, currentDate.dayOfMonth
    )
    return remember(currentDate) { datePickerDialog }
}


@Composable
fun TransactionsSortBottomSheet(
    onClose: () -> Unit,
    possibleSortOrders: List<String>,
    selectedSortOrder: SortOrder,
    onSelectedSortOrder: (Int) -> Unit
) {
    BaseBottomSheet(
        title = "Sortera efter",
        onClose = onClose
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SegmentedButton(
                items = possibleSortOrders,
                selectedIndex = selectedSortOrder.ordinal,
                indexChanged = { onSelectedSortOrder(it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(onClick = { onClose() }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }

}