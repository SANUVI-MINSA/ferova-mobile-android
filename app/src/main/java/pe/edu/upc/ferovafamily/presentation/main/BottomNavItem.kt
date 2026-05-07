package pe.edu.upc.ferovafamily.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(MainRoutes.HOME, "Inicio", Icons.Default.Home)
    data object Diary : BottomNavItem(MainRoutes.DIARY, "Diario", Icons.Default.MenuBook)
    data object Appointments : BottomNavItem(MainRoutes.APPOINTMENTS, "Citas", Icons.Default.CalendarMonth)
    data object Consultations : BottomNavItem(MainRoutes.CONSULTATIONS_TAB, "Consultas", Icons.AutoMirrored.Filled.Chat)

    companion object {
        val items = listOf(Home, Diary, Appointments, Consultations)
    }
}