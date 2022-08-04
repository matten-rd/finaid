package com.strand.finaid.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    shape: Shape = RoundedCornerShape(16.dp),
    transaction: TransactionUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit
) {
    SwipeableBaseItem(
        modifier = modifier,
        cardColors = cardColors,
        shape = shape,
        icon = transaction.icon,
        color = transaction.color,
        header = transaction.memo,
        subhead = "${transaction.date} \u2022 ${transaction.category}",
        amount = transaction.amount,
        onEditClick = { onEditClick(transaction.id) },
        onDeleteClick = { onDeleteClick(transaction) }
    )
}

@Composable
fun SavingsAccountItem(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    shape: Shape = RoundedCornerShape(16.dp),
    savingsAccount: SavingsAccountUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (SavingsAccountUiState) -> Unit
) {
    SwipeableBaseItem(
        modifier = modifier,
        cardColors = cardColors,
        shape = shape,
        icon = savingsAccount.icon,
        color = savingsAccount.color,
        header = savingsAccount.name,
        subhead = savingsAccount.bank,
        amount = savingsAccount.amount,
        onEditClick = { onEditClick(savingsAccount.id) },
        onDeleteClick = { onDeleteClick(savingsAccount) }
    )
}


@Composable
private fun SwipeableBaseItem(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    shape: Shape = RoundedCornerShape(16.dp),
    icon: ImageVector,
    color: Color,
    header: String,
    subhead: String,
    amount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 72.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1, sizePx to 2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .size(48.dp)
                .clickable {
                    onDeleteClick()
                    scope.launch { swipeableState.animateTo(0) }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .size(48.dp)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        BaseItem(
            modifier = modifier.offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) },
            shape = shape,
            icon = icon,
            color = color,
            header = header,
            subhead = subhead,
            amount = amount,
            onEditClick = onEditClick
        )
    }
}


@Composable
private fun BaseItem(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    shape: Shape = RoundedCornerShape(16.dp),
    icon: ImageVector,
    color: Color,
    header: String,
    subhead: String,
    amount: Int,
    onEditClick: () -> Unit
) {
    var multiSelected by remember { mutableStateOf(false) }
    val cardContainerColor by animateColorAsState(
        if (multiSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onEditClick,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(color)
                        .size(48.dp)
                        .selectable(multiSelected, onClick = { multiSelected = !multiSelected }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (multiSelected) Icons.Rounded.Check else icon,
                        contentDescription = null,
                        tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
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
) {
    HomeScreenBaseListItem(
        icon = savingsAccount.icon,
        color = savingsAccount.color,
        header = savingsAccount.name,
        subhead = savingsAccount.bank,
        amount = savingsAccount.amount
    )
}

@Composable
fun HomeScreenTransactionItem(
    transaction: TransactionUiState,
) {
    HomeScreenBaseListItem(
        icon = transaction.icon,
        color = transaction.color,
        header = transaction.memo,
        subhead = "${transaction.date} \u2022 ${transaction.category}",
        amount = transaction.amount
    )
}


@Composable
private fun HomeScreenBaseListItem(
    icon: ImageVector,
    color: Color,
    header: String,
    subhead: String,
    amount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
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
                        .size(42.dp),
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


