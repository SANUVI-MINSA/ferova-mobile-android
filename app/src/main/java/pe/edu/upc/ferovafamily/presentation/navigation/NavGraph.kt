package pe.edu.upc.ferovafamily.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.UserApiService
import pe.edu.upc.ferovafamily.data.remote.dto.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.JsonObject
import pe.edu.upc.ferovafamily.presentation.auth.CreateAccountScreen
import pe.edu.upc.ferovafamily.presentation.auth.LoginScreen
import pe.edu.upc.ferovafamily.presentation.main.MainRoutes
import pe.edu.upc.ferovafamily.presentation.main.MainScreen
import pe.edu.upc.ferovafamily.presentation.shared.NewPasswordScreen
import pe.edu.upc.ferovafamily.presentation.shared.RecoveryPasswordScreen
import pe.edu.upc.ferovafamily.presentation.shared.VerificationScreen
// Asegúrate de que estas rutas de importación coincidan con tu estructura
import pe.edu.upc.ferovafamily.presentation.shared.AyudaScreen
import pe.edu.upc.ferovafamily.presentation.shared.SeguridadScreen
import pe.edu.upc.ferovafamily.presentation.shared.PrivacidadScreen
import pe.edu.upc.ferovafamily.presentation.treatment_tracking.TreatmentTrackingScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Auto-login con la cuenta de demo si aún no hay token guardado
    LaunchedEffect(Unit) {
        val tokenManager = TokenManager.getInstance(context)
        if (tokenManager.token == null) {
            withContext(Dispatchers.IO) {
                try {
                    val service = FerovaApiClient.create(UserApiService::class.java, context)
                    val resp = service.login(LoginRequest("12345678", "Ferova2024!"))
                    if (resp.isSuccessful) {
                        val token = resp.body()!!.token
                        tokenManager.token = token
                        // Decodificar JWT para obtener userId
                        val payloadB64 = token.split(".").getOrNull(1) ?: return@withContext
                        val decoded = String(
                            android.util.Base64.decode(payloadB64, android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING),
                            Charsets.UTF_8
                        )
                        val userId = Gson().fromJson(decoded, JsonObject::class.java)
                            ?.get("id")?.asString ?: return@withContext
                        tokenManager.userId = userId
                        // Obtener nombre del usuario
                        val userResp = service.getUserById(userId)
                        if (userResp.isSuccessful) {
                            val user = userResp.body()!!
                            tokenManager.userName     = user.name
                            tokenManager.userLastName = user.lastname
                            tokenManager.userRole     = user.role
                            tokenManager.userEmail    = user.email
                        }
                    }
                } catch (_: Exception) { /* sin red: continuar con datos mock */ }
            }
        }
    }

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
                },
                onNavigateToAyuda = {
                    navController.navigate(AyudaRoute)
                },
                onNavigateToSeguridad = {
                    navController.navigate(SeguridadRoute)
                },
                onNavigateToPrivacidad = {
                    navController.navigate(PrivacidadRoute)
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
                },
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
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

        composable<AyudaRoute> {
            AyudaScreen(onBack = { navController.popBackStack() })
        }

        composable<SeguridadRoute> {
            SeguridadScreen(onBack = { navController.popBackStack() })
        }

        composable<PrivacidadRoute> {
            PrivacidadScreen(onBack = { navController.popBackStack() })
        }
    }
}