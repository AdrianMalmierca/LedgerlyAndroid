package adrian.malmierca.ledgerlyandroid.di

import adrian.malmierca.ledgerlyandroid.data.notifications.NotificationService
import adrian.malmierca.ledgerlyandroid.data.repository.AuthRepositoryImpl
import adrian.malmierca.ledgerlyandroid.data.repository.ExpenseRepositoryImpl
import adrian.malmierca.ledgerlyandroid.domain.repository.AuthRepository
import adrian.malmierca.ledgerlyandroid.domain.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import adrian.malmierca.ledgerlyandroid.data.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
        AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ExpenseRepository = ExpenseRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService =
        NotificationService(context)
}