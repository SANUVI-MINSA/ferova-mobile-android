package pe.edu.upc.ferovafamily.presentation.main

import android.R.attr.type
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.edu.upc.ferovafamily.presentation.consultations.ConsultationsRoutes
import pe.edu.upc.ferovafamily.presentation.consultations.screens.ChatScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.ConsultationsScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.MyConsultationsScreen
import pe.edu.upc.ferovafamily.presentation.consultations.screens.NewConsultationScreen
import pe.edu.upc.ferovafamily.presentation.home.HomeScreen
import pe.edu.upc.ferovafamily.presentation.progress.ProgressRoutes
import pe.edu.upc.ferovafamily.presentation.progress.screens.DoseConfirmedScreen
import pe.edu.upc.ferovafamily.presentation.progress.screens.MedalUnlockedScreen
import pe.edu.upc.ferovafamily.presentation.progress.screens.ProgressScreen
import pe.edu.upc.ferovafamily.presentation.progress.screens.StreakLostScreen
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsRoutes
import pe.edu.upc.ferovafamily.presentation.appointments.screens.AppointmentConfirmedScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.HealthCenterDetailScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.HealthCentersMapScreen
import pe.edu.upc.ferovafamily.presentation.appointments.screens.TimeSlotSelectionScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryRoutes
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NewNutritionalMealScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NutritionalDiaryScreen
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens.NutritionalHistoryScreen

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@Composable
fun MainScreen(
    // Agregamos este parámetro para comunicarnos con el NavGraph principal
    onNavigateToHistory: () -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // El bottom bar sólo se muestra en las pantallas raíz de cada tab
    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }

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
                    onLogout = {
                        // TODO: cuando lo implemente el equipo, regresar al login
                    }
                )
            }

            // Tab: Diario (placeholder)
            composable(MainRoutes.DIARY) {
                NutritionalDiaryScreen (
                    onNewFoodEntry = {
                        navController.navigate(NutritionalDiaryRoutes.NEW_MEAL)
                    },
                    onSeeFoodHistory = { selectedPatient ->
                        navController.navigate("diary_history/$selectedPatient")
                    }
                )
            }

            // Tab: Citas (placeholder)
            composable(MainRoutes.APPOINTMENTS) {
                PlaceholderScreen("Citas")
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

            composable (route= PatientManagementRoutes.CREATE_PATIENT) {
                CreatePatientScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterChild = {navController.popBackStack()}
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

            composable(ProgressRoutes.DOSE_CONFIRMED) {
                DoseConfirmedScreen(
                    onContinueToHome = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    },
                    onSeeProgress = {
                        navController.navigate(ProgressRoutes.PROGRESS) {
                            popUpTo(ProgressRoutes.PROGRESS) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = ProgressRoutes.MEDAL_UNLOCKED,
                arguments = listOf(navArgument("medalId") { type = NavType.StringType })
            ) { backStack ->
                val medalId = backStack.arguments?.getString("medalId") ?: return@composable
                MedalUnlockedScreen(
                    medalId = medalId,
                    onBackToHome = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    },
                    onSeeMedals = {
                        navController.navigate(ProgressRoutes.PROGRESS) {
                            popUpTo(ProgressRoutes.PROGRESS) { inclusive = true }
                        }
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

            // ──────────── SUBPANTALLAS: DIARIO NUTRICIONAL ────────────

            composable(NutritionalDiaryRoutes.NEW_MEAL) {
                NewNutritionalMealScreen(
                    onBack = {navController.popBackStack()},
                    onRegisterMeal = {navController.popBackStack()}
                )
            }

            composable("diary_history/{patientName}",
                arguments = listOf(navArgument("patientName") {type = NavType.StringType}))
            { backStackEntry ->
                val patientName = backStackEntry.arguments?.getString("patientName")?: ""
                NutritionalHistoryScreen(
                    selectedPatient = patientName,
                    onBack = {
                        navController.popBackStack()
                    })
            }

            // ──────────── SUBPANTALLAS: CITAS Y POSTAS ────────────

            composable(AppointmentsRoutes.HEALTH_CENTERS_MAP) {
                HealthCentersMapScreen(
                    onBack = { navController.popBackStack() },
                    onCenterClick = { centerId ->
                        navController.navigate(AppointmentsRoutes.healthCenterDetail(centerId))
                    }
                )
            }

            composable(
                route = AppointmentsRoutes.HEALTH_CENTER_DETAIL,
                arguments = listOf(navArgument("centerId") { type = NavType.StringType })
            ) { backStack ->
                val centerId = backStack.arguments?.getString("centerId") ?: return@composable
                HealthCenterDetailScreen(
                    centerId = centerId,
                    onBack = { navController.popBackStack() },
                    onBookAppointment = { id ->
                        navController.navigate(AppointmentsRoutes.appointmentBooking(id))
                    }
                )
            }

            composable(
                route = AppointmentsRoutes.APPOINTMENT_BOOKING,
                arguments = listOf(navArgument("centerId") { type = NavType.StringType })
            ) { backStack ->
                val centerId = backStack.arguments?.getString("centerId") ?: return@composable
                AppointmentBookingScreen(
                    centerId = centerId,
                    onBack = { navController.popBackStack() },
                    onContinue = { patientId, dateIso ->
                        navController.navigate(
                            AppointmentsRoutes.timeSlotSelection(centerId, patientId, dateIso)
                        )
                    }
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
                    onConfirm = { appointmentId ->
                        navController.navigate(AppointmentsRoutes.appointmentConfirmed(appointmentId)) {
                            popUpTo(MainRoutes.CONSULTATIONS_TAB)
                        }
                    }
                )
            }

            composable(
                route = AppointmentsRoutes.APPOINTMENT_CONFIRMED,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStack ->
                val appointmentId = backStack.arguments?.getString("appointmentId") ?: return@composable
                AppointmentConfirmedScreen(
                    appointmentId = appointmentId,
                    onBackToHome = {
                        navController.navigate(MainRoutes.HOME) {
                            popUpTo(MainRoutes.HOME) { inclusive = true }
                        }
                    }
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