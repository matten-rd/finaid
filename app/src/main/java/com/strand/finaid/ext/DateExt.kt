package com.strand.finaid.ext

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

// Converters
fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

fun Date.toLocalDate(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.toMillis(): Long = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()


// Formatters
fun Date.formatDayMonthYear(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
fun LocalDate.formatDayMonthYear(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun LocalDate.formatMonthYear(): String = this.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
fun Date.formatMonthYear(): String = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(this)

fun LocalDate.formatYear(): String = this.format(DateTimeFormatter.ofPattern("yyyy", Locale.getDefault()))

fun LocalDate.formatShortMonth(): String = this.format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault()))