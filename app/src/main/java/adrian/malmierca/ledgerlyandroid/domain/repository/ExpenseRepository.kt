package adrian.malmierca.ledgerlyandroid.domain.repository

import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
    suspend fun deleteAllExpensesForUser(userId: String): Result<Unit>
}