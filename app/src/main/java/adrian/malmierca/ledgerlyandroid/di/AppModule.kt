package adrian.malmierca.ledgerlyandroid.di

import adrian.malmierca.ledgerlyandroid.data.repository.AuthRepositoryImpl
import adrian.malmierca.ledgerlyandroid.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //because the class provides dependencies
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides //to say how to create an object
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance() //create and give FirebaseAuth

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
        AuthRepositoryImpl(auth) //create the repository
}