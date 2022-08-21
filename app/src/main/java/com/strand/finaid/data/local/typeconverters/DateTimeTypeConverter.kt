package com.strand.finaid.data.local.typeconverters

import androidx.room.TypeConverter
import java.util.*

object DateTimeTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}