package com.strand.finaid.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T?) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}

fun <T> Flow<T>.asResult() : Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}
