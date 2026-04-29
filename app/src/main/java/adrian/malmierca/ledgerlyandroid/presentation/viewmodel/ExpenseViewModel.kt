package adrian.malmierca.ledgerlyandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.domain.repository.AuthRepository
import adrian.malmierca.ledgerlyandroid.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchText: String = "",
    val selectedCategory: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState(isLoading = true)) //we dont have the data yet, so we load
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            combine( //its executed when one change, to group flows
                expenseRepository.getExpenses(),
                _searchText,
                _selectedCategory
            ) { expenses, search, category ->
                val filtered = expenses.filter { expense ->
                    val matchesCategory = category == null || expense.category == category
                    val matchesSearch = search.isEmpty() ||
                            expense.title.contains(search, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
                ExpenseUiState(
                    expenses = expenses,
                    filteredExpenses = filtered,
                    isLoading = false,
                    searchText = search,
                    selectedCategory = category
                )
            }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage
                    )
                }
                .collect { state -> //active the flow and receive the values
                    _uiState.value = state
                }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            val expense = Expense(
                title = title,
                amount = amount,
                date = Date(),
                category = category,
                userId = authRepository.getCurrentUserId() ?: ""
            )
            expenseRepository.addExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense.id)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            expenseRepository.deleteAllExpensesForUser(userId)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}