package com.strand.finaid.data.repository.impl

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asCategory
import com.strand.finaid.data.mapper.asNetworkCategory
import com.strand.finaid.data.model.Category
import com.strand.finaid.data.network.CategoriesNetworkDataSource
import com.strand.finaid.data.network.model.NetworkCategory
import com.strand.finaid.data.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val network: CategoriesNetworkDataSource
) : CategoriesRepository {

    override fun getCategoriesStream(userId: String): Flow<Result<List<Category>>> {
        return network.addCategoriesListener(userId, deleted = false)
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

    override fun getDeletedCategoriesStream(userId: String): Flow<Result<List<Category>>> {
        return network.addCategoriesListener(userId, deleted = true)
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

    override fun saveCategory(
        userId: String,
        category: Category,
        onResult: (Throwable?) -> Unit
    ) {
        network.saveCategory(userId, category.asNetworkCategory(), onResult)
    }

    override fun moveCategoryToTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.moveCategoryToTrash(userId, categoryId, onResult)
    }

    override fun restoreCategoryFromTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.restoreCategoryFromTrash(userId, categoryId, onResult)
    }

    override fun deleteCategoryPermanently(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.deleteCategoryPermanently(userId, categoryId, onResult)
    }

}