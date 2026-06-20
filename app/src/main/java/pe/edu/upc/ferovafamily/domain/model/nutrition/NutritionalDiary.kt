package pe.edu.upc.ferovafamily.domain.model.nutrition

import java.util.Date

data class TodayDiary(
    val diaryId: String?,
    val date: String,
    val totalIronAbsorbed: Double,
    val foodEntries: List<FoodEntry>
)

data class NutritionalHistory(
    val patientId: String,
    val period: HistoryPeriod,
    val days: List<DaySummary>
)

data class HistoryPeriod(
    val startDate: String,
    val endDate: String
)