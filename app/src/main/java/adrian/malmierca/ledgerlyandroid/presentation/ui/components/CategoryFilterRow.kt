package adrian.malmierca.ledgerlyandroid.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import adrian.malmierca.ledgerlyandroid.R
import androidx.compose.ui.res.dimensionResource

@Composable
fun CategoryFilterRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Food" to R.string.category_food,
        "Transport" to R.string.category_transport,
        "Bills" to R.string.category_bills,
        "Other" to R.string.category_other
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        items(categories) { (key, labelRes) ->
            FilterChip(
                selected = selectedCategory == key,
                onClick = { onCategorySelected(key) },
                label = { Text(stringResource(labelRes)) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}