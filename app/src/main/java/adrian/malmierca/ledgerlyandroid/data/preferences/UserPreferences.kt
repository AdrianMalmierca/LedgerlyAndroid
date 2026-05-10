package adrian.malmierca.ledgerlyandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton //because datastore is global
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val CURRENCY = stringPreferencesKey("currency")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: false }
    val notificationHour: Flow<Int> = context.dataStore.data.map { it[NOTIFICATION_HOUR] ?: 20 }
    val currency: Flow<String> = context.dataStore.data.map { it[CURRENCY] ?: "€" }

    //suspend because we save in datastore so it can takes time
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled } //we save the value (enabled) with the key(DARL_MODE)
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationHour(hour: Int) {
        context.dataStore.edit { it[NOTIFICATION_HOUR] = hour }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[CURRENCY] = currency }
    }
}