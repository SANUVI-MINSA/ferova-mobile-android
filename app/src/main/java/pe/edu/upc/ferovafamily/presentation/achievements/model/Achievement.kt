package pe.edu.upc.ferovafamily.presentation.achievements.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val isUnlocked: Boolean,
    val dateObtained: String? = null,
    val category: AchievementCategory,
    val longDescription: String = ""
)

enum class AchievementCategory(val label: String) {
    STREAK("Racha"),
    NUTRITION("Nutrición"),
    CONSULTATIONS("Consultas"),
    DOSE("Dosis")
}