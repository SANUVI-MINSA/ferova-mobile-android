package pe.edu.upc.ferovafamily.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.ferovafamily.presentation.auth.CreateAccountScreen
import pe.edu.upc.ferovafamily.presentation.auth.LoginScreen
import pe.edu.upc.ferovafamily.presentation.main.MainRoutes
import pe.edu.upc.ferovafamily.presentation.main.MainScreen
import pe.edu.upc.ferovafamily.presentation.shared.NewPasswordScreen
import pe.edu.upc.ferovafamily.presentation.shared.RecoveryPasswordScreen
import pe.edu.upc.ferovafamily.presentation.shared.VerificationScreen
// Asegúrate de que estas rutas de importación coincidan con tu estructura
import pe.edu.upc.ferovafamily.presentation.treatment_tracking.TreatmentTrackingScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onNavigateToCreateAccount = {
                    navController.navigate(CreateAccountRoute)
                },
                onNavigateToHome = {
                    navController.navigate(MainRoutes.MAIN) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToRecovery = {
                    navController.navigate(RecoveryPasswordRoute)
                }
            )
        }

        composable<CreateAccountRoute> {
            CreateAccountScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<RecoveryPasswordRoute> {
            RecoveryPasswordScreen(
                onNavigateToVerification = {
                    navController.navigate(VerificationRoute)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<VerificationRoute> {
            VerificationScreen(
                onNavigateToNewPassword = {
                    navController.navigate(NewPasswordRoute)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NewPasswordRoute> {
            NewPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(MainRoutes.MAIN) {
            MainScreen(
                onNavigateToHistory = {
                    navController.navigate(TreatmentTrackingRoute)
                }
            )
        }

        composable<TreatmentTrackingRoute> {
            TreatmentTrackingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}