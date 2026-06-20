package pe.edu.upc.ferovafamily.presentation.main

import pe.edu.upc.ferovafamily.presentation.appointments.screens.AppointmentBookingScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.presentation.consultations.ConsultationsRoutes
import pe.edu.upc.ferovafamily.presentation.consultations.screens.ChatScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.ConsultationsScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.MyConsultationsScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.NewConsultationScreen
import pe.edu.upc.ferovafamily.presentation.home.HomeScreen
import pe.edu.upc.ferovafamily.presentation.progress.ProgressRoutes
import pe.edu.upc.ferovafamily.presentation.progress.screens.ProgressScreen
import pe.edu.upc.ferovafamily.presentation.progress.screens.StreakLostScreen
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsRoutes
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.presentation.appointments.screens.AppointmentConfirmedScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.AppointmentsScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.HealthCenterDetailScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.HealthCentersMapScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.TimeSlotSelectionScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryRoutes
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NewNutritionalMealScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NutritionalDiaryScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NutritionalHistoryScreen
import pe.edu.upc.ferovafamily.presentation.notifications.NotificationsScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.presentation.patient_management.PatientManagementRoutes
import pe.edu.upc.ferovafamily.presentation.patient_management.screens.CreatePatientScreen

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@Composable
fun MainScreen(
    onNavigateToHistory: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val appointmentsViewModel: AppointmentsViewModel = viewModel()
    val nutritionalDiaryViewModel: NutritionalDiaryViewModel = viewModel()

    // El bottom bar sólo se muestra en las pantallas raíz de cada tab
    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }

    val nutritionalUiState by nutritionalDiaryViewModel.uiState.collectAsState()

    val selectedPatientId = nutritionalUiState.selectedPatient?.id ?: ""

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(MainRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Crimson,
                                selectedTextColor = Crimson,
                                indicatorColor = Cream,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainRoutes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ──────────────── TABS ────────────────

            // Tab: Inicio
            composable(MainRoutes.HOME) {
                HomeScreen(
                    onNavigateToAchievements = {
                        navController.navigate(ProgressRoutes.PROGRESS)
                    },
                    onNavigateToNewMeal = {
                        navController.navigate(NutritionalDiaryRoutes.NEW_MEAL)
                    },
                    onNavigateToCreatePatient = {
                        navController.navigate(PatientManagementRoutes.CREATE_PATIENT)
                    },
                    onNavigateToHistory = onNavigateToHistory,

                    onNavigateToHealthCenters = {
                        navController.navigate(AppointmentsRoutes.HEALTH_CENTERS_MAP)
                    },
                    onLogout = onLogout
                )
            }

            // ════════════════════════════════════════════════════════════════
            // Tab: Diario Nutricional
            // ════════════════════════════════════════════════════════════════

            composable(MainRoutes.DIARY) {
                // Obtener patientId del TokenManager o usar default
                val context = LocalContext.current
                val tokenManager = remember(context) {
                    TokenManager.getInstance(context)
                }
                val patientId = tokenManager.userId ?: "patient-id-default"

                NutritionalDiaryScreen(
                    patientId = selectedPatientId,
                    viewModel = nutritionalDiaryViewModel,   // instancia compartida
                    onNewFoodEntry = {
                        navController.navigate(NutritionalDiaryRoutes.NEW_MEAL)
                    },
                    onSeeFoodHistory = { selectedPatientId ->
                        navController.navigate(
                            "${NutritionalDiaryRoutes.HISTORY}/$selectedPatientId"
                        )
                    }
                )
            }

            // Tab: Citas — mapa de postas + flujo de agendar cita
            composable(MainRoutes.APPOINTMENTS) {
                AppointmentsScreen(
                    onScheduleAppointment = {
                        navController.navigate(AppointmentsRoutes.HEALTH_CENTERS_MAP)
                    },
                    viewModel = appointmentsViewModel
                )
            }

            composable(MainRoutes.CONSULTATIONS_TAB) {
                ConsultationsScreen(
                    onWriteForChild = { childId ->
                        navController.navigate(ConsultationsRoutes.newConsultation(childId))
                    },
                    onOpenChat = { consultationId ->
                        navController.navigate(ConsultationsRoutes.chat(consultationId))
                    },
                    onGoToMyConsultations = {
                        navController.navigate(ConsultationsRoutes.MY_CONSULTATIONS)
                    },
                    onSeeNearbyHealthCenters = {
                        navController.navigate(AppointmentsRoutes.HEALTH_CENTERS_MAP)
                    }
                )
            }

            // ──────────── SUBPANTALLAS: CONSULTAS ────────────

            composable(ConsultationsRoutes.MY_CONSULTATIONS) {
                MyConsultationsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenChat = { id ->
                        navController.navigate(ConsultationsRoutes.chat(id))
                    }
                )
            }

            composable(
                route = ConsultationsRoutes.NEW_CONSULTATION,
                arguments = listOf(navArgument("childId") { type = NavType.StringType })
            ) { backStack ->
                val childId = backStack.arguments?.getString("childId") ?: return@composable
                NewConsultationScreen(
                    childId = childId,
                    onBack = { navController.popBackStack() },
                    onConsultationCreated = { id ->
                        navController.navigate(ConsultationsRoutes.chat(id)) {
                            popUpTo(MainRoutes.CONSULTATIONS_TAB)
                        }
                    }
                )
            }

            composable(
                route = ConsultationsRoutes.CHAT,
                arguments = listOf(navArgument("consultationId") { type = NavType.StringType })
            ) { backStack ->
                val consultationId = backStack.arguments?.getString("consultationId")
                    ?: return@composable
                ChatScreen(
                    consultationId = consultationId,
                    onBack = { navController.popBackStack() }
                )
            }

            // ──────────── SUBPANTALLA: CREACION DE PACIENTE ────────────

            composable(route = PatientManagementRoutes.CREATE_PATIENT) {
                CreatePatientScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterChild = { navController.popBackStack() }
                )
            }

            // ──────────── SUBPANTALLAS: PROGRESO Y MEDALLAS ────────────

            composable(ProgressRoutes.PROGRESS) {
                ProgressScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    },
                    onPreviewDoseConfirmed = {
                        navController.navigate(ProgressRoutes.DOSE_CONFIRMED)
                    },
                    onPreviewMedalUnlocked = { id ->
                        navController.navigate(ProgressRoutes.medalUnlocked(id))
                    },
                    onPreviewStreakLost = {
                        navController.navigate(ProgressRoutes.STREAK_LOST)
                    }
                )
            }


            composable(ProgressRoutes.STREAK_LOST) {
                StreakLostScreen(
                    onStartAgain = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            // ════════════════════════════════════════════════════════════════
            // SUBPANTALLAS: DIARIO NUTRICIONAL
            // ════════════════════════════════════════════════════════════════

            // Nueva entrada de alimento
            composable(NutritionalDiaryRoutes.NEW_MEAL) {
                val context = LocalContext.current
                val tokenManager = remember(context) {
                    TokenManager.getInstance(context)
                }
                val patientId = tokenManager.userId ?: "patient-id-default"

                NewNutritionalMealScreen(
                    patientId = selectedPatientId,
                    viewModel = nutritionalDiaryViewModel,   // misma instancia compartida
                    onBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // Historial nutricional con filtro de fechas
            composable(
                route = "${NutritionalDiaryRoutes.HISTORY}/{patientId}",
                arguments = listOf(navArgument("patientId") { type = NavType.StringType })
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                NutritionalHistoryScreen(
                    patientId = patientId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ──────────── SUBPANTALLAS: CITAS Y POSTAS ────────────

            composable(
                route = AppointmentsRoutes.HEALTH_CENTERS_MAP
            ) {
                HealthCentersMapScreen(
                    onCenterClick = { centerId ->
                        navController.navigate(AppointmentsRoutes.healthCenterDetail(centerId))
                    },
                    viewModel = appointmentsViewModel
                )
            }

            composable(
                route = AppointmentsRoutes.HEALTH_CENTER_DETAIL,
                arguments = listOf(navArgument("centerId") { type = NavType.StringType })
            ) { backStack ->
                val centerId = backStack.arguments?.getString("centerId")!!
                HealthCenterDetailScreen(
                    centerId = centerId,
                    onBack = { navController.popBackStack() },
                    onBookAppointment = { id ->
                        navController.navigate(AppointmentsRoutes.appointmentBooking(id))
                    },
                    viewModel = appointmentsViewModel
                )
            }

            composable(
                route = AppointmentsRoutes.APPOINTMENT_BOOKING,
                arguments = listOf(navArgument("centerId") { type = NavType.StringType })
            ) { backStack ->
                val centerId = backStack.arguments?.getString("centerId")!!
                AppointmentBookingScreen(
                    centerId = centerId,
                    onBack = { navController.popBackStack() },
                    onContinue = { patientId, dateIso ->
                        appointmentsViewModel.resetBookingState()
                        navController.navigate(
                            AppointmentsRoutes.timeSlotSelection(centerId, patientId, dateIso)
                        )
                    },
                    viewModel = appointmentsViewModel
                )
            }

            composable(
                route = AppointmentsRoutes.TIME_SLOT_SELECTION,
                arguments = listOf(
                    navArgument("centerId") { type = NavType.StringType },
                    navArgument("patientId") { type = NavType.StringType },
                    navArgument("dateIso") { type = NavType.StringType }
                )
            ) { backStack ->
                val centerId = backStack.arguments?.getString("centerId") ?: return@composable
                val patientId = backStack.arguments?.getString("patientId") ?: return@composable
                val dateIso = backStack.arguments?.getString("dateIso") ?: return@composable

                TimeSlotSelectionScreen(
                    centerId = centerId,
                    patientId = patientId,
                    dateIso = dateIso,
                    onBack = { navController.popBackStack() },
                    viewModel = appointmentsViewModel,
                    onConfirm = { appointmentId ->
                        navController.navigate(AppointmentsRoutes.appointmentConfirmed(appointmentId)) {
                            popUpTo(MainRoutes.APPOINTMENTS)
                        }
                    }
                )
            }

            composable(
                route = AppointmentsRoutes.APPOINTMENT_CONFIRMED,
            ) {
                AppointmentConfirmedScreen(
                    onBackToHome = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    },
                    viewModel = appointmentsViewModel
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name — En construcción",
            color = Color.Gray
        )
    }
}
