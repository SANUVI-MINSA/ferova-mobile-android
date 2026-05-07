package pe.edu.upc.ferovafamily.presentation.progress

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upc.ferovafamily.presentation.progress.model.HemoglobinPoint
import pe.edu.upc.ferovafamily.presentation.progress.model.Medal
import pe.edu.upc.ferovafamily.presentation.progress.model.MedalType
import pe.edu.upc.ferovafamily.presentation.progress.model.ProgressStats

class ProgressViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProgressStats())
    val state: StateFlow<ProgressStats> = _state.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val medals = listOf(
            Medal(
                id = "first-week",
                type = MedalType.FIRST_WEEK,
                title = "First Week",
                description = "Completa los 7 días sin fallar",
                isUnlocked = true,
                currentDays = 7,
                targetDays = 7,
                celebrationMessage = "Has completado el tratamiento de 7 días exitosamente"
            ),
            Medal(
                id = "first-month",
                type = MedalType.FIRST_MONTH,
                title = "First Month",
                description = "Completa los 30 días sin fallar",
                isUnlocked = true,
                currentDays = 30,
                targetDays = 30,
                celebrationMessage = "Has completado el tratamiento de 30 días exitosamente. ¡Continúa así!"
            ),
            Medal(
                id = "half-treatment",
                type = MedalType.HALF_TREATMENT,
                title = "Half Treatment",
                description = "Completa la mitad del tratamiento (45 días)",
                isUnlocked = false,
                currentDays = 30,
                targetDays = 45,
                celebrationMessage = "Has completado el tratamiento de la mitad del tratamiento exitosamente. ¡Continúa así!"
            ),
            Medal(
                id = "treatment-completed",
                type = MedalType.TREATMENT_COMPLETED,
                title = "Treatment Completed",
                description = "Completa el tratamiento (90 días)",
                isUnlocked = false,
                currentDays = 30,
                targetDays = 90,
                celebrationMessage = "Has completado el tratamiento de 90 días exitosamente"
            ),
            Medal(
                id = "streak-recovered",
                type = MedalType.STREAK_RECOVERED,
                title = "Streak Recovered",
                description = "Recupera tu racha perdida (7 días)",
                isUnlocked = false,
                currentDays = 0,
                targetDays = 7,
                celebrationMessage = "¡Felicidades! Has recuperado tu racha semanal. Tu compromiso es admirable"
            )
        )

        val hemoglobinHistory = listOf(
            HemoglobinPoint("12 Abril", 7.0f),
            HemoglobinPoint("14 Mayo", 8.0f),
            HemoglobinPoint("12 Junio", 9.0f),
            HemoglobinPoint("28 Julio", 11.2f)
        )

        _state.value = ProgressStats(
            healthStatus = "Activo",
            totalPoints = 70,
            currentStreak = 7,
            longestStreak = 30,
            currentHemoglobin = 11.2f,
            hemoglobinHistory = hemoglobinHistory,
            medals = medals
        )
    }

    fun getMedalById(id: String): Medal? =
        _state.value.medals.firstOrNull { it.id == id }
}