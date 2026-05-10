package adrian.malmierca.ledgerlyandroid

import android.app.Application
import adrian.malmierca.ledgerlyandroid.data.notifications.NotificationService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LedgerlyApp : Application() {

    @Inject
    lateinit var notificationService: NotificationService //here because here we put things we do once when ew run

    override fun onCreate() {
        super.onCreate()
        notificationService.createNotificationChannel()
    }
}