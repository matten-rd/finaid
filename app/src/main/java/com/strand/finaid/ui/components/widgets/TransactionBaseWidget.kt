package com.strand.finaid.ui.components.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ext.formatMonthYear
import com.strand.finaid.ext.formatYear
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.charts.Period
import com.strand.finaid.ui.components.charts.PeriodStateHolder

@Composable
fun TransactionBaseWidget(
    displayValue: Int,
    periodStateHolder: PeriodStateHolder,
    content: @Composable ColumnScope.() -> Unit
) {
    val periodState by periodStateHolder.periodStateFlow.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            shape = RoundedCornerShape(28.dp)
        ) {
            SegmentedButton(
                modifier = Modifier.padding(12.dp),
                items = Period.values().map { stringResource(id = it.periodId) },
                selectedIndex = periodState.period.ordinal,
                indexChanged = periodStateHolder::onSetPeriod
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val dateText = when (periodState.period) {
                        Period.Month -> periodState.selectedMonth.formatMonthYear()
                        Period.Year -> periodState.selectedYear.formatYear()
                        Period.Total -> ""
                    }
                    Text(text = "${displayValue.formatAmount()} kr", style = MaterialTheme.typography.headlineMedium)
                    Text(text = dateText, style = MaterialTheme.typography.bodySmall)
                }

                if (periodState.period != Period.Total) {
                    Row {
                        val decrement = {
                            if (periodState.period == Period.Year)
                                periodStateHolder.decrementYear()
                            else
                                periodStateHolder.decrementMonth()
                        }
                        val increment = {
                            if (periodState.period == Period.Year)
                                periodStateHolder.incrementYear()
                            else
                                periodStateHolder.incrementMonth()
                        }
                        FilledIconButton(onClick = decrement) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        FilledIconButton(onClick = increment) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
            content()
        }
    }
}