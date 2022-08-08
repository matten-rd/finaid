package com.strand.finaid.ui.components.list_items

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlin.math.absoluteValue

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    transaction: TransactionUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit
) {
    val revealState = rememberRevealState()
    val isDragged by remember {
        derivedStateOf { revealState.offset.value.absoluteValue > 1f }
    }
    val backgroundColor: Color by animateColorAsState(
        if (isDragged) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background
    )

    RevealSwipe(
        modifier = modifier,
        revealState = revealState,
        onStartClick = { /*TODO*/ },
        onEndClick = { onDeleteClick(transaction) },
        hiddenIconEnd = Icons.Default.Delete,
        hiddenIconStart = Icons.Rounded.ContentCopy,
        endBackgroundColor = MaterialTheme.colorScheme.error
    ) {
        BaseItem(
            cardColors = CardDefaults.cardColors(containerColor = backgroundColor),
            icon = transaction.icon,
            color = transaction.color,
            header = transaction.memo,
            subhead = "${transaction.date} \u2022 ${transaction.category}",
            amount = transaction.amount,
            onClick = { onEditClick(transaction.id) }
        )
    }
}

@Composable
fun SavingsAccountItem(
    modifier: Modifier = Modifier,
    savingsAccount: SavingsAccountUiState,
    onEditClick: (String) -> Unit,
    onDeleteClick: (SavingsAccountUiState) -> Unit
) {
    val revealState = rememberRevealState()
    val isDragged by remember {
        derivedStateOf { revealState.offset.value.absoluteValue > 1f }
    }
    val backgroundColor: Color by animateColorAsState(
        if (isDragged) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background
    )

    RevealSwipe(
        modifier = modifier,
        revealState = revealState,
        onStartClick = { /*TODO*/ },
        onEndClick = { onDeleteClick(savingsAccount) },
        hiddenIconEnd = Icons.Default.Delete,
        hiddenIconStart = Icons.Rounded.ContentCopy,
        endBackgroundColor = MaterialTheme.colorScheme.error
    ) {
        BaseItem(
            cardColors = CardDefaults.cardColors(containerColor = backgroundColor),
            icon = savingsAccount.icon,
            color = savingsAccount.color,
            header = savingsAccount.name,
            subhead = savingsAccount.bank,
            amount = savingsAccount.amount,
            onClick = { onEditClick(savingsAccount.id) }
        )
    }
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
    var multiSelected by remember { mutableStateOf(false) }
    val cardContainerColor by animateColorAsState(
        if (multiSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    )

    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = cardColors
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
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


