package com.strand.finaid.ui.export

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.ext.formatMonthYear
import com.strand.finaid.ui.components.textfield.FinaidTextField
import com.strand.finaid.ui.transactions.ReadonlyTextField
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


@Composable
fun ExportScreen(
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    val pickerState = rememberMonthYearPickerState(initialSelectedDate = uiState.date)

    val visible = remember { mutableStateOf(false) }
    if (visible.value) {
        MonthYearPicker(
            pickerState = pickerState,
            onDateChange = viewModel::onDateChange
        ) {
            visible.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReadonlyTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.date.formatMonthYear(),
                onClick = { visible.value = true },
                label = "Välj månad",
                supportingText = "Transaktioner t.o.m. den valda månaden exporteras.",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )

            FinaidTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.sheetName,
                onValueChange = viewModel::onSheetNameChange,
                label = "Bladnamn (valfritt)",
                supportingText = "Döps till \"${uiState.date.formatMonthYear()}\" om det lämnas tomt."
            )


            Button(
                onClick = viewModel::onExportClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.FileUpload,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Exportera")
            }
        }
    }

}

@Composable
fun MonthYearPicker(
    pickerState: MonthYearPickerState,
    onDateChange: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { pickerState.year-- }) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                    }

                    AnimatedContent(
                        targetState = pickerState.year,
                        transitionSpec = {
                            // Compare the incoming number with the previous number.
                            if (targetState > initialState) {
                                slideInHorizontally { height -> height/2 } + fadeIn() with
                                        slideOutHorizontally { height -> -height/2 } + fadeOut()
                            } else {
                                slideInHorizontally { height -> -height/2 } + fadeIn() with
                                        slideOutHorizontally { height -> height/2 } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        }
                    ) { targetYear ->
                        Text(text = "$targetYear")
                    }

                    IconButton(onClick = { pickerState.year++ }) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    maxItemsInEachRow = 3
                ) {
                    (1..pickerState.shortMonthNames.size).forEach {
                        MonthItem(pickerState = pickerState, monthValue = it, year = pickerState.year)
                    }
                }

                // Buttons
                Box(modifier = Modifier.align(Alignment.End)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = "Cancel")
                        }
                        TextButton(
                            onClick = {
                                onDateChange(pickerState.selectedDate)
                                onDismissRequest()
                            }
                        ) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthItem(
    pickerState: MonthYearPickerState,
    monthValue: Int,
    year: Int
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(60.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (pickerState.monthToday == monthValue && pickerState.yearToday == year) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = { pickerState.month = monthValue }),
        contentAlignment = Alignment.Center
    ) {

        val animatedSize by animateDpAsState(
            targetValue = if (pickerState.month == monthValue) 60.dp else 0.dp,
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearOutSlowInEasing
            )
        )

        Box(
            modifier = Modifier
                .size(animatedSize)
                .background(
                    color = if (pickerState.month == monthValue) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                )
        )

        Text(
            text = pickerState.shortMonthNames[monthValue-1],
            color = if (pickerState.month == monthValue) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}


@Stable
class MonthYearPickerState(
    initialSelectedDate: LocalDate
) {
    private val dfs = DateFormatSymbols(Locale.getDefault())
    val shortMonthNames: Array<String> = dfs.shortMonths

    private val _selectedDate by mutableStateOf(initialSelectedDate)
    var month by mutableStateOf(_selectedDate.monthValue)
    var year by mutableStateOf(_selectedDate.year)
    val selectedDate: LocalDate by derivedStateOf {
        YearMonth.of(year, month).atEndOfMonth()
    }

    private val dateToday = LocalDate.now()
    val yearToday = dateToday.year
    val monthToday = dateToday.monthValue
}

@Composable
fun rememberMonthYearPickerState(
    initialSelectedDate: LocalDate
): MonthYearPickerState = remember(initialSelectedDate) {
    MonthYearPickerState(initialSelectedDate)
}




