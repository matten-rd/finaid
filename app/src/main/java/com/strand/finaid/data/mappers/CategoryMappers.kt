package com.strand.finaid.data.mappers

import com.strand.finaid.data.local.entities.CategoryEntity
import com.strand.finaid.data.models.Category
import com.strand.finaid.ext.asColor
import com.strand.finaid.ext.asHexCode

fun Category.asCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        hexCode = color.asHexCode(),
        deleted = deleted,
        transactionType = transactionType
    )
}

fun CategoryEntity.asCategory(): Category {
    return Category(
        id = id,
        name = name,
        color = hexCode.asColor(),
        deleted = deleted,
        transactionType = transactionType
    )
}