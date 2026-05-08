package pe.edu.upc.ferovafamily.presentation.achievements

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upc.ferovafamily.presentation.achievements.model.Achievement
import pe.edu.upc.ferovafamily.presentation.achievements.model.AchievementCategory

data class AchievementsUiState(
    val totalPoints: Int = 0,
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val achievements: List<Achievement> = emptyList()
)

class AchievementsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val achievements = listOf(
            Achievement(
                id = "ach-1",
                title = "Primera Dosis",
                description = "Completaste tu primera dosis de hierro",
                longDescription = "¡Felicidades! Completaste con éxito la primera dosis " +
                        "de hierro de tu hijo. Este es el primer paso de un largo camino " +
                        "hacia la recuperación. Sigue así para mantener una racha saludable.",
                points = 10,
                isUnlocked = true,
                dateObtained = "12 de marzo, 2026",
                category = AchievementCategory.DOSE
            ),
            Achievement(
                id = "ach-2",
                title = "Racha de 5 días",
                description = "5 días consecutivos administrando la dosis",
                longDescription = "Has mantenido la dosis por 5 días seguidos. La " +
                        "constancia es clave en el tratamiento contra la anemia. ¡Tu " +
                        "compromiso está marcando la diferencia!",
                points = 25,
                isUnlocked = true,
                dateObtained = "5 de abril, 2026",
                category = AchievementCategory.STREAK
            ),
            Achievement(
                id = "ach-3",
                title = "Primera Consulta",
                description = "Iniciaste tu primera consulta con la enfermera",
                longDescription = "Iniciar el diálogo con tu enfermera asignada es " +
                        "fundamental para resolver dudas y tener un tratamiento personalizado.",
                points = 15,
                isUnlocked = true,
                dateObtained = "20 de abril, 2026",
                category = AchievementCategory.CONSULTATIONS
            ),
            Achievement(
                id = "ach-4",
                title = "Racha de 15 días",
                description = "15 días consecutivos sin fallar",
                longDescription = "Una racha de 15 días demuestra disciplina y " +
                        "compromiso. Sigue así para alcanzar la próxima meta.",
                points = 50,
                isUnlocked = false,
                category = AchievementCategory.STREAK
            ),
            Achievement(
                id = "ach-5",
                title = "Nutricionista Familiar",
                description = "Registra 30 comidas en el diario",
                longDescription = "Llevar un registro detallado de la alimentación " +
                        "ayuda a la enfermera a darte mejores recomendaciones.",
                points = 40,
                isUnlocked = false,
                category = AchievementCategory.NUTRITION
            ),
            Achievement(
                id = "ach-6",
                title = "Maestro del Hierro",
                description = "Alcanza 100mg de hierro absorbido en un mes",
                longDescription = "Llegar a esta meta nutricional es un gran logro. " +
                        "Significa que las elecciones de alimentos están funcionando.",
                points = 75,
                isUnlocked = false,
                category = AchievementCategory.NUTRITION
            )
        )

        _uiState.value = AchievementsUiState(
            totalPoints = achievements.filter { it.isUnlocked }.sumOf { it.points },
            unlockedCount = achievements.count { it.isUnlocked },
            totalCount = achievements.size,
            achievements = achievements
        )
    }

    fun getAchievementById(id: String): Achievement? =
        _uiState.value.achievements.firstOrNull { it.id == id }
}