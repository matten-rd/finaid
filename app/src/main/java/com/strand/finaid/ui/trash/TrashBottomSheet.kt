package com.strand.finaid.ui.trash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.strand.finaid.R
import com.strand.finaid.ui.components.BaseBottomSheet
import com.strand.finaid.ui.components.list_items.IconListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TrashBottomSheet(
    viewModel: TrashViewModel,
    bottomSheetState: ModalBottomSheetState,
    scope: CoroutineScope
) {
    BaseBottomSheet {
        Column(modifier = Modifier.navigationBarsPadding()) {
            IconListItem(
                icon = Icons.Default.Restore,
                text = stringResource(id = R.string.restore)
            ) {
                when (viewModel.selectedTrashType) {
                    TrashType.Savings -> viewModel.setIsSavingsAccountRestoreDialogOpen(true)
                    TrashType.Transactions -> viewModel.setIsTransactionRestoreDialogOpen(true)
                    TrashType.Categories -> viewModel.setIsCategoryRestoreDialogOpen(true)
                }
                scope.launch { bottomSheetState.hide() }
            }
            IconListItem(
                icon = Icons.Default.DeleteForever,
                text = stringResource(id = R.string.delete_permanently)
            ) {
                when (viewModel.selectedTrashType) {
                    TrashType.Savings -> viewModel.setIsSavingsAccountDeleteDialogOpen(true)
                    TrashType.Transactions -> viewModel.setIsTransactionDeleteDialogOpen(true)
                    TrashType.Categories -> viewModel.setIsCategoryDeleteDialogOpen(true)
                }
                scope.launch { bottomSheetState.hide() }
            }
        }
    }
}