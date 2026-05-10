package adrian.malmierca.ledgerlyandroid.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import adrian.malmierca.ledgerlyandroid.R
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryBills
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryFood
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryOther
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryTransport
import androidx.compose.ui.res.dimensionResource

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    val (color, labelRes) = when (category) {
        "Food" -> CategoryFood to R.string.category_food
        "Transport" -> CategoryTransport to R.string.category_transport
        "Bills" -> CategoryBills to R.string.category_bills
        else -> CategoryOther to R.string.category_other
    }

    Surface(
        modifier = modifier.padding(top = 4.dp),
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = stringResource(labelRes),
            color = color,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small), vertical = dimensionResource(R.dimen.padding_xsmall))
        )
    }
}