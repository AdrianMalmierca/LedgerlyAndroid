package adrian.malmierca.ledgerlyandroid.presentation.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import adrian.malmierca.ledgerlyandroid.R
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.ExpenseViewModel
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryBills
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryFood
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryOther
import adrian.malmierca.ledgerlyandroid.ui.theme.CategoryTransport
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun BarChart(
    categoryTotals: Map<String, Double>,
    categoryColors: Map<String, androidx.compose.ui.graphics.Color>,
    modifier: Modifier = Modifier
) {
    if (categoryTotals.isEmpty()) return

    val maxValue = categoryTotals.values.max()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
    ) {
        val barWidth = size.width / (categoryTotals.size * 2f)
        val spacing = barWidth
        val maxBarHeight = size.height - 40.dp.toPx() //to px because canvas works with px no dp

        categoryTotals.entries.forEachIndexed { index, (category, value) ->
            val barHeight = if (maxValue > 0) (value / maxValue * maxBarHeight).toFloat() else 0f //we normalize
            // the value (value/maxvalue), cause it will be a value between 0 and 1, and we sum the max
            // height to transform into pixels. So the biggest bar will get the maxBarHeight
            val x = index * (barWidth + spacing) + spacing / 2 //with index we put each bar depending their
            // position, we have the max space (barWidth + spacing) and spacing / 2 is the initial margin to
            // locate into the center
            val color = categoryColors[category] ?: androidx.compose.ui.graphics.Color.Gray

            //Bar
            drawRoundRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = x,
                    y = size.height - barHeight - 30.dp.toPx() //start at the bottom of the canvas (size.height)
                // and we leave a small margin for the text on the top
                ),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )

            //Value on top of the bar
            drawContext.canvas.nativeCanvas.drawText( //access to androids canvas
                "%.0f€".format(value),
                x + barWidth / 2, //the beginning of the bar (x) and in the center
                size.height - barHeight - 35.dp.toPx(), //from the bottom of the canvas, we go to the top
                //and we add an extra margin so the text doesnt touch the bar
                android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 28f
                    setColor(android.graphics.Color.GRAY)
                    isFakeBoldText = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseChartScreen(
    bottomPadding: PaddingValues = PaddingValues(),
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val categoryTotals = uiState.expenses
        .groupBy { it.category }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

    val total = categoryTotals.values.sum()

    val categoryColors = mapOf(
        "Food" to CategoryFood,
        "Transport" to CategoryTransport,
        "Bills" to CategoryBills,
        "Other" to CategoryOther
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottomPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Total card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.total_amount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "%.2f €".format(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        //Chart
        if (categoryTotals.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.tab_chart),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BarChart(
                        categoryTotals = categoryTotals,
                        categoryColors = categoryColors
                    )
                    //Leyend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        categoryTotals.keys.forEach { category ->
                            val color = categoryColors[category] ?: CategoryOther
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(10.dp),
                                    shape = CircleShape,
                                    color = color
                                ) {}
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}