package com.strand.finaid.ui.components.charts

import androidx.annotation.StringRes
import com.strand.finaid.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate


enum class Period(@StringRes val periodId: Int) {
    Month(R.string.month),
    Year(R.string.year),
    Total(R.string.total)
}

data class PeriodState(
    val period: Period = Period.Total,
    val selectedYear: LocalDate = LocalDate.now(),
    val selectedMonth: LocalDate = LocalDate.now()
)

class PeriodStateHolder(initialState: PeriodState = PeriodState()) {
    private val _periodStateFlow = MutableStateFlow(initialState)
    val periodStateFlow = _periodStateFlow.asStateFlow()

    fun onSetPeriod(newValue: Int) {
        val newPeriod = Period.values()[newValue]
        _periodStateFlow.update { it.copy(period = newPeriod) }
    }

    fun incrementYear() {
        _periodStateFlow.update { it.copy(selectedYear = it.selectedYear.plusYears(1)) }
    }

    fun decrementYear() {
        _periodStateFlow.update { it.copy(selectedYear = it.selectedYear.minusYears(1)) }
    }

    fun incrementMonth() {
        _periodStateFlow.update { it.copy(selectedMonth = it.selectedMonth.plusMonths(1)) }
    }

    fun decrementMonth() {
        _periodStateFlow.update { it.copy(selectedMonth = it.selectedMonth.minusMonths(1)) }
    }
}