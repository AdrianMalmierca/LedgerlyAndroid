package adrian.malmierca.ledgerlyandroid.presentation.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import adrian.malmierca.ledgerlyandroid.R
import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.presentation.ui.expenses.ExpenseChartScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.expenses.ExpenseListScreen
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.ExpenseViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (Expense) -> Unit,
    onLogout: () -> Unit,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_expenses)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_chart)) }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.new_expense_title),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> ExpenseListScreen(
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToDetail = onNavigateToDetail,
                onLogout = onLogout,
                bottomPadding = innerPadding,
                showAddDialog = showAddDialog,
                onDismissAddDialog = { showAddDialog = false },
                onSaveExpense = { title, amount, category ->
                    expenseViewModel.addExpense(title, amount, category)
                    showAddDialog = false
                }
            )
            1 -> ExpenseChartScreen(
                bottomPadding = innerPadding
            )
        }
    }
}