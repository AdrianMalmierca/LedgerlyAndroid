package adrian.malmierca.ledgerlyandroid.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import adrian.malmierca.ledgerlyandroid.domain.model.Expense
import adrian.malmierca.ledgerlyandroid.presentation.ui.auth.LoginScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.auth.SignUpScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.expenses.ExpenseDetailScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.main.MainScreen
import adrian.malmierca.ledgerlyandroid.presentation.ui.settings.SettingsScreen
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.AuthViewModel
import adrian.malmierca.ledgerlyandroid.presentation.viewmodel.ExpenseViewModel

sealed class Screen(val route: String) { //sealed cause we have route then, we use type safety...
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ExpenseList : Screen("expense_list")
    object ExpenseDetail : Screen("expense_detail")
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

    //shared expense for detail screen
    var selectedExpense: Expense? = null

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
            MainScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDetail = { expense ->
                    selectedExpense = expense
                    navController.navigate(Screen.ExpenseDetail.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ExpenseDetail.route) {
            selectedExpense?.let { expense ->
                ExpenseDetailScreen(
                    expense = expense,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Settings.route) {
            val expenseViewModel: ExpenseViewModel = hiltViewModel()
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onDeleteAccount = { password, onError ->
                    expenseViewModel.deleteAccount(
                        password = password,
                        onComplete = {
                            authViewModel.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onError = onError
                    )
                }
            )
        }
    }
}