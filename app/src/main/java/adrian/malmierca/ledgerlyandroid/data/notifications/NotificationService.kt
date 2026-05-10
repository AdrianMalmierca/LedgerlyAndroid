package adrian.malmierca.ledgerlyandroid.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import adrian.malmierca.ledgerlyandroid.R

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "ledgerly_reminders"
        const val WORK_NAME = "daily_expense_reminder"
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_title),
            NotificationManager.IMPORTANCE_DEFAULT //priority
        ).apply {
            description = context.getString(R.string.notification_body)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager //get the notification service manager, using the Hilt context
        manager.createNotificationChannel(channel) //register the cannel
    }

    fun scheduleDailyReminder(hour: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1) //if has already passed, tomorrow
        }

        val delay = target.timeInMillis - now.timeInMillis //time to the notification in ms

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS) //the worker is executed every 24h
            .setInitialDelay(delay, TimeUnit.MILLISECONDS) //wait until the first exact hour the first time
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork( //programme the worker
            WORK_NAME, //only one, we dont have duplicates
            ExistingPeriodicWorkPolicy.UPDATE, //replace if it already exits
            request
        )
    }

    fun cancelReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) { //it executes the background task

    override fun doWork(): Result {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, NotificationService.CHANNEL_ID) //it creates the notification using the channel, with the workers context
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.notification_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) //it closes when we touch
            .build()

        manager.notify(1, notification) //show the notification
        return Result.success()
    }
}