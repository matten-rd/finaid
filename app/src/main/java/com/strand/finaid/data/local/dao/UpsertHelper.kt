package com.strand.finaid.data.local.dao

/**
 * Performs an upsertMany by first attempting to insertOrIgnoreTransactionEntity [items] using [insertMany] with the the result
 * of the inserts returned.
 *
 * Items that were not inserted due to conflicts are then updated using [updateMany]
 */
suspend fun <T> upsertMany(
    items: List<T>,
    insertMany: suspend (List<T>) -> List<Long>,
    updateMany: suspend (List<T>) -> Unit,
) {
    val insertResults = insertMany(items)

    val updateList = items.zip(insertResults)
        .mapNotNull { (item, insertResult) ->
            if (insertResult == -1L) item else null
        }
    if (updateList.isNotEmpty()) updateMany(updateList)
}

suspend fun <T> upsert(
    item: T,
    insert: suspend (T) -> Long,
    update: suspend (T) -> Unit
) {
    val insertResult = insert(item)
    if (insertResult == -1L) update(item)
}