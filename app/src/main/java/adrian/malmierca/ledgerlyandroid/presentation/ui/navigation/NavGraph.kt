package adrian.malmierca.ledgerlyandroid.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import adrian.malmierca.ledgerlyandroid.presentation.ui.auth.LoginScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.auth.SignUpScreen
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ExpenseList : Screen("expense_list")
    object Settings : Screen("settings")
}

@Composable
fun LedgerlyNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination = if (authState.isLoggedIn) {
        Screen.ExpenseList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.ExpenseList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } //to delete screens
                        //until login included because we dont want to go back once we're logged
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.ExpenseList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ExpenseList.route) {
            androidx.compose.material3.Text("Expense List — coming soon")
        }

        composable(Screen.Settings.route) {
            androidx.compose.material3.Text("Settings — coming soon")
        }
    }
}