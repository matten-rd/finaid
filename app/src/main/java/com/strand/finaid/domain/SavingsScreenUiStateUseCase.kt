package com.strand.finaid.domain

import com.strand.finaid.data.Result
import com.strand.finaid.data.asResult
import com.strand.finaid.data.mappers.asSavingsAccountUiState
import com.strand.finaid.data.models.SavingsAccount
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.ui.savings.SavingsAccountUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class SavingsScreenUiState(
    val savingsAccounts: List<SavingsAccountUiState>? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)

class SavingsScreenUiStateUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    operator fun invoke(deleted: Boolean = false): Flow<SavingsScreenUiState> {
        val savingsAccountsStream: Flow<List<SavingsAccount>> =
            if (deleted)
                savingsRepository.getDeletedSavingsAccountsStream()
            else
                savingsRepository.getSavingsAccountsStream()

        return savingsAccountsStream
            .asResult()
            .map { result: Result<List<SavingsAccount>> ->
                when (result) {
                    is Result.Success -> {
                        SavingsScreenUiState(result.data?.map { it.asSavingsAccountUiState() }, isLoading = false, isError = false)
                    }
                    Result.Loading -> SavingsScreenUiState(null, isLoading = true, isError = false)
                    is Result.Error -> SavingsScreenUiState(null, isLoading = false, isError = true)
                }
            }
    }
}