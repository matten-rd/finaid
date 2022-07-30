package com.strand.finaid.model.service.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.model.data.Transaction
import kotlinx.coroutines.tasks.await

class TransactionsPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, Transaction>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Transaction>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Transaction> {
        return try {
            val currentPage = params.key ?: query.get().await()
            val lastVisible = currentPage.documents[currentPage.size() - 1]
            val nextPage = query.startAfter(lastVisible).get().await()

            val data = currentPage.toObjects<Transaction>()
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}