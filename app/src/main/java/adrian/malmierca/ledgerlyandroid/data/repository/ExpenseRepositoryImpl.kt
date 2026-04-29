package adrian.malmierca.ledgerlyandroid.data.repository

import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ExpenseRepository {

    //we put get to always have the current user, otherwise it could change, we dont save the value in memory, we recalculate it
    private val currentUserId get() = auth.currentUser?.uid ?: ""

    private fun userExpensesCollection() =
        firestore.collection("users")
            .document(currentUserId)
            .collection("expenses")

    override fun getExpenses(): Flow<List<Expense>> = callbackFlow {
        val listener = userExpensesCollection()
            .orderBy("date", Query.Direction.DESCENDING)
            //in case the values in firestore change, they update automatically, so we dont have to call again to the API
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            date = doc.getDate("date") ?: Date(),
                            category = doc.getString("category") ?: "Other",
                            userId = doc.getString("userId") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(expenses)  //send the updated list to the UI
            }
        awaitClose { listener.remove() } //to avoid leaks and consume
    }

    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val data = hashMapOf(
                "title" to expense.title,
                "amount" to expense.amount,
                "date" to expense.date,
                "category" to expense.category,
                "userId" to currentUserId
            )
            userExpensesCollection().add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            userExpensesCollection().document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllExpensesForUser(userId: String): Result<Unit> {
        return try {
            val docs = userExpensesCollection().get().await()
            val batch = firestore.batch() //batch to delete a lot of data
            docs.documents.forEach { batch.delete(it.reference) }
            batch.commit().await() //execute all  changes
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}