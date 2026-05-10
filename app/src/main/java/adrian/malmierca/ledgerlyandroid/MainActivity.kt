package adrian.malmierca.ledgerlyandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import adrian.malmierca.ledgerlyandroid.data.preferences.UserPreferences
import adrian.malmierca.ledgerlyandroid.presentation.ui.navigation.LedgerlyNavGraph
import adrian.malmierca.ledgerlyandroid.ui.theme.LedgerlyAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences //here and not in LedgerlyApp because UserPreferences
    //emits a flow which affects to the theme

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission() //open the android system to request permission
    ) { _ -> } //the user can accept or refuse, we manage silently

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //to have full screen, more modern, and to control de bars control
        requestNotificationPermission()
        setContent {
            val darkMode by userPreferences.darkMode.collectAsStateWithLifecycle(false)
            LedgerlyAndroidTheme(darkTheme = darkMode) {
                LedgerlyNavGraph(navController = rememberNavController())
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}