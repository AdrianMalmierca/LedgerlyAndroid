package adrian.malmierca.ledgerlyandroid.presentation.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import adrian.malmierca.ledgerlyandroid.R
import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.presentation.ui.components.CategoryChip
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryBills
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryFood
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryOther
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryTransport
import androidx.compose.ui.res.dimensionResource
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expense: Expense,
    onNavigateBack: () -> Unit
) {
    val categoryColor: Color = when (expense.category) {
        "Food" -> CategoryFood
        "Transport" -> CategoryTransport
        "Bills" -> CategoryBills
        else -> CategoryOther
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(expense.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //Amount header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.box_height)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.total_amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                    Text(
                        text = "%.2f €".format(expense.amount),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            //Details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_big)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
            ) {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = expense.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        CategoryChip(category = expense.category)
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                    DetailRow(
                        label = stringResource(R.string.expense_date),
                        value = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                            .format(expense.date)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xmedium)))
                    HorizontalDivider()

                    DetailRow(
                        label = stringResource(R.string.section_category),
                        value = expense.category,
                        valueColor = categoryColor
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xmedium)))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xmedium)))

                    DetailRow(
                        label = stringResource(R.string.section_amount),
                        value = "%.2f €".format(expense.amount)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (valueColor == Color.Unspecified)
                MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}