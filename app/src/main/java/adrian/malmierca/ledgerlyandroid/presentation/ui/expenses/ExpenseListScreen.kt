package adrian.malmierca.ledgerlyandroid.presentation.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import adrian.malmierca.ledgerlyandroid.R
import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.presentation.ui.components.CategoryFilterRow
import adrian.malmierca.ledgerlyandroid.presentation.ui.components.ExpenseItem
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.AuthViewModel
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.ExpenseViewModel
import androidx.compose.ui.res.dimensionResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (Expense) -> Unit,
    onLogout: () -> Unit,
    bottomPadding: PaddingValues = PaddingValues(),
    showAddDialog: Boolean = false,
    onDismissAddDialog: () -> Unit = {},
    onSaveExpense: (String, Double, String) -> Unit = { _, _, _ -> },
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by expenseViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ledgerly") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                    IconButton(onClick = {
                        authViewModel.signOut()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = stringResource(R.string.logout_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(), //scafold padding
            bottom = innerPadding.calculateBottomPadding() + bottomPadding.calculateBottomPadding() //we sum both
            //cause otherwise one of them would be ignored
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(combinedPadding)
        ) {
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = { expenseViewModel.onSearchTextChange(it) },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                singleLine = true,
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium), vertical = dimensionResource(R.dimen.padding_small))
            )

            CategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { expenseViewModel.onCategorySelected(it) },
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_xmedium))
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_expenses),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    items(
                        items = uiState.filteredExpenses,
                        key = { it.id }
                    ) { expense ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    expenseViewModel.deleteExpense(expense)
                                    true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {},
                            enableDismissFromStartToEnd = false
                        ) {
                            ExpenseItem(
                                expense = expense,
                                onClick = { onNavigateToDetail(expense) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddExpenseDialog(
                onDismiss = onDismissAddDialog,
                onSave = onSaveExpense
            )
        }
    }
}