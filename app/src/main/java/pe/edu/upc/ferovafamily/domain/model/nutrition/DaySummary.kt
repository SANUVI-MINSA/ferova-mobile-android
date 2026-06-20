package pe.edu.upc.ferovafamily.domain.model.nutrition

data class DaySummary (
    val date: String,
    val displayDate: String,
    val totalIronAbsorbed: Double,
    val hasInhibitor: Boolean,
    val inhibitorCount: Int,
    val totalFoodEntries: Int
)