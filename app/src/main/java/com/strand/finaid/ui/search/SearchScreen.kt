package com.strand.finaid.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.data.models.Category
import com.strand.finaid.ui.components.SegmentedButton

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchScreenType by viewModel.searchScreenType

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedIconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Sort, contentDescription = null)
            }
            SegmentedButton(
                items = viewModel.searchScreens,
                selectedIndex = searchScreenType.ordinal,
                indexChanged = viewModel::onSearchScreenTypeChange
            )
        }

        SearchTransactionsContent(
            categories = viewModel.categories
        )
    }
}

@Composable
private fun SearchTransactionsContent(
    categories: List<Category>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.width(8.dp)) }
        items(categories, key = { it.id }) { category ->
            var selected by remember { mutableStateOf(false) }
            FilterChip(
                selected = selected,
                onClick = { selected = !selected },
                label = { Text(text = category.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = category.color
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }
        item { Spacer(modifier = Modifier.width(8.dp)) }
    }


}
