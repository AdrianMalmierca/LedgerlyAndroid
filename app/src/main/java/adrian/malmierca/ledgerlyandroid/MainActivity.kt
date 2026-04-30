package adrian.malmierca.ledgerlyandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import adrian.malmierca.ledgerlyandroid.presentation.ui.navigation.LedgerlyNavGraph
import adrian.malmierca.ledgerlyandroid.ui.theme.LedgerlyAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LedgerlyAndroidTheme {
                LedgerlyNavGraph(navController = rememberNavController())
            }
        }
    }
}