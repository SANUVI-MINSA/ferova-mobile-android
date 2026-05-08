package pe.edu.upc.ferovafamily.presentation.progress.model

data class ProgressStats(
    val healthStatus: String = "Activo",
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val currentHemoglobin: Float = 0f,
    val hemoglobinHistory: List<HemoglobinPoint> = emptyList(),
    val medals: List<Medal> = emptyList()
)