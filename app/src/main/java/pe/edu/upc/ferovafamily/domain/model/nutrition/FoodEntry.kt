package pe.edu.upc.ferovafamily.domain.model.nutrition

import java.util.Date

data class FoodEntry(
    val entryId: String,
    val foodName: String,
    val quantity: Int,
    val unit: String,
    val ironAbsorbed: Double,
    val isInhibitor: Boolean
)

data class RegisterFoodEntryResult(
    val success: Boolean,
    val message: String,
    val foodEntry: FoodEntry,
    val newTotalIronAbsorbed: Double,
    val warningMessage: String?
)
