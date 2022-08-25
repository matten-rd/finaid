package com.strand.finaid.data.repository.impl

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.data.Result
import com.strand.finaid.data.mappers.asCategory
import com.strand.finaid.data.mappers.asNetworkCategory
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.CategoriesNetworkDataSource
import com.strand.finaid.data.network.models.NetworkCategory
import com.strand.finaid.data.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val network: CategoriesNetworkDataSource
) : CategoriesRepository {

    override fun addCategoriesListener(
        userId: String,
        deleted: Boolean
    ): Flow<Result<List<Category>>> {
        return network.addCategoriesListener(userId, deleted)
            .map { result: Result<QuerySnapshot> ->
                when (result) {
                    is Result.Error -> result
                    Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<NetworkCategory>()?.map { it.asCategory() }
                    )
                }
            }
    }

    override fun getCategories(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkCategory) -> Unit
    ) {
        network.getCategories(userId, onError, onSuccess)
    }

    override fun addCategory(
        userId: String,
        category: Category,
        onResult: (Throwable?) -> Unit
    ) {
        network.addCategory(userId, category.asNetworkCategory(), onResult)
    }

    override fun moveCategoryToTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.moveCategoryToTrash(userId, categoryId, onResult)
    }

    override fun deleteCategoryPermanently(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.deleteCategoryPermanently(userId, categoryId, onResult)
    }

    override fun getDeletedCategories(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkCategory) -> Unit
    ) {
        network.getDeletedCategories(userId, onError, onSuccess)
    }

    override fun restoreCategoryFromTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.restoreCategoryFromTrash(userId, categoryId, onResult)
    }

}