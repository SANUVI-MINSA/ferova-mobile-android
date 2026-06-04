package pe.edu.upc.ferovafamily.domain.model

data class AchievementProgress(
    val points: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val healthStatus: String
)
