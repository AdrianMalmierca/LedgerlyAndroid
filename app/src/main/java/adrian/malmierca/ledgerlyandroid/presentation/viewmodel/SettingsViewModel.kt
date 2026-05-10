package adrian.malmierca.ledgerlyandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import adrian.malmierca.ledgerlyandroid.data.notifications.NotificationService
import adrian.malmierca.ledgerlyandroid.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val notificationHour: Int = 20,
    val currency: String = "€"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences,
    private val notificationService: NotificationService
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.darkMode,
        preferences.notificationsEnabled,
        preferences.notificationHour,
        preferences.currency
    ) { darkMode, notifications, hour, currency ->
        SettingsUiState(
            darkMode = darkMode,
            notificationsEnabled = notifications,
            notificationHour = hour,
            currency = currency
        )
    }.stateIn(
        scope = viewModelScope, //while the viewmodel lives
        started = SharingStarted.WhileSubscribed(5000), //only activate when someone observed, otherwise timeout of 5s
        initialValue = SettingsUiState() //before get real data
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { preferences.setDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
            if (enabled) {
                notificationService.scheduleDailyReminder(uiState.value.notificationHour)
            } else {
                notificationService.cancelReminder()
            }
        }
    }

    fun setNotificationHour(hour: Int) {
        viewModelScope.launch {
            preferences.setNotificationHour(hour)
            if (uiState.value.notificationsEnabled) {
                notificationService.scheduleDailyReminder(hour)
            }
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch { preferences.setCurrency(currency) }
    }
}