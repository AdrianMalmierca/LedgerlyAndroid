package adrian.malmierca.ledgerlyandroid.data.repository

import adrian.malmierca.ledgerlyandroid.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await() //await to wait until is finished, with success or with error
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No user logged in"))
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reauthenticate(email: String, password: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No user logged in"))
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserEmail(): String? = auth.currentUser?.email
}