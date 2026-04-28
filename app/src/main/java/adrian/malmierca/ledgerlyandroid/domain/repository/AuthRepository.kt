package adrian.malmierca.ledgerlyandroid.domain.repository

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    fun signOut()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
}