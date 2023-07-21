package com.strand.finaid.ui.components.list_items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.TransactionUiState

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    transaction: TransactionUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDuplicateClick: (String) -> Unit
) {
    val currentItem by rememberUpdatedState(transaction)
    val dismissState = rememberDismissState(
        positionalThreshold = { totalDistance -> 0.5f * totalDistance },
        confirmValueChange = {
            // return true for it to go off screen, false to make it bounce back
            when (it) {
                DismissValue.DismissedToStart -> {
                    onDeleteClick(currentItem.id)
                    true
                }
                DismissValue.DismissedToEnd -> {
                    onDuplicateClick(currentItem.id)
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            SwipeBackground(dismissState = dismissState)
        },
        dismissContent = {
            BaseItem(
                icon = transaction.icon,
                color = transaction.color,
                header = transaction.memo,
                subhead = "${transaction.date} \u2022 ${transaction.category}",
                amount = transaction.amount,
                onClick = { onEditClick(transaction.id) }
            )
        }
    )
}

@Composable
fun SavingsAccountItem(
    modifier: Modifier = Modifier,
    savingsAccount: SavingsAccountUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val currentItem by rememberUpdatedState(savingsAccount)
    val dismissState = rememberDismissState(
        positionalThreshold = { totalDistance -> 0.5f * totalDistance },
        confirmValueChange = {
            // return true for it to go off screen, false to make it bounce back
            when (it) {
                DismissValue.DismissedToStart -> {
                    onDeleteClick(currentItem.id)
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            SwipeBackground(dismissState = dismissState)
        },
        dismissContent = {
            BaseItem(
                icon = savingsAccount.icon,
                color = savingsAccount.color,
                header = savingsAccount.name,
                subhead = savingsAccount.bank,
                amount = savingsAccount.amount,
                onClick = { onEditClick(savingsAccount.id) }
            )
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}

@Composable
internal fun BaseItem(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    icon: ImageVector,
    color: Color,
    header: String,
    subhead: String,
    amount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = cardColors,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .drawBehind { drawRoundRect(color) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                    )
                }
                Column(
                    modifier = Modifier.height(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = subhead,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${amount.formatAmount()} kr", style = MaterialTheme.typography.titleLarge)
        }
    }
}


@Composable
fun HomeScreenSavingsAccountItem(
    savingsAccount: SavingsAccountUiState,
    isFirst: Boolean = false,
    isLast: Boolean = false,
) {
    HomeScreenBaseListItem(
        icon = savingsAccount.icon,
        color = savingsAccount.color,
        header = savingsAccount.name,
        subhead = savingsAccount.bank,
        amount = savingsAccount.amount,
        isFirst = isFirst,
        isLast = isLast
    )
}

@Composable
fun HomeScreenTransactionItem(
    transaction: TransactionUiState,
    isFirst: Boolean = false,
    isLast: Boolean = false,
) {
    HomeScreenBaseListItem(
        icon = transaction.icon,
        color = transaction.color,
        header = transaction.memo,
        subhead = "${transaction.date} \u2022 ${transaction.category}",
        amount = transaction.amount,
        isFirst = isFirst,
        isLast = isLast
    )
}


@Composable
private fun HomeScreenBaseListItem(
    icon: ImageVector,
    color: Color,
    header: String,
    subhead: String,
    amount: Int,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(16.dp)
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        isLast -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        else -> RoundedCornerShape(4.dp)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = subhead,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${amount.formatAmount()} kr", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun IconListItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp)
            .clickable(onClick = onClick).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = text)
    }
}


