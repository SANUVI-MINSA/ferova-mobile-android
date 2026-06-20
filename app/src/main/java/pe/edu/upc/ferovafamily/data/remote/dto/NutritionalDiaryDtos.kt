package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ═══════════════════════════════════════════════════════════════════════════
// REGISTRO DE ALIMENTO (POST /food-entry)
// ═══════════════════════════════════════════════════════════════════════════
//QUEDA
data class RegisterFoodEntryRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("foodItemId") val foodItemId: String,
    @SerializedName("quantity") val quantity: Int,
)



// ═══════════════════════════════════════════════════════════════════════════
// DIARIO DE HOY (GET /today/{patientId})
// ═══════════════════════════════════════════════════════════════════════════
//QUEDA
data class TodayDiaryResponse(
    @SerializedName("diaryId") val diaryId: String?,
    @SerializedName("date") val date: String,
    @SerializedName("totalIronAbsorbed") val totalIronAbsorbed: Double,
    @SerializedName("foodEntries") val foodEntries: List<FoodEntryItemDto>
)

data class FoodEntryItemDto(
    @SerializedName("entryId") val entryId: String,
    @SerializedName("foodName") val foodName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("ironAbsorbed") val ironAbsorbed: Double,
    @SerializedName("isInhibitor") val isInhibitor: Boolean
)

data class FoodEntryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("foodEntry") val foodEntry: FoodEntryDto,
    @SerializedName("newTotalIronAbsorbed") val newTotalIronAbsorbed: Double,
    @SerializedName("warningMessage") val warningMessage: String?
)

data class FoodEntryDto(
    @SerializedName("id") val entryId: String,
    @SerializedName("foodName") val foodName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("ironAbsorbed") val ironAbsorbed: Double,
    @SerializedName("isInhibitor") val isInhibitor: Boolean
)


// ═══════════════════════════════════════════════════════════════════════════
// HISTORIAL NUTRICIONAL (GET /history/{patientId})
// ═══════════════════════════════════════════════════════════════════════════
//QUEDA
data class NutritionalHistoryResponse(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("period") val period: PeriodDto,
    @SerializedName("days") val days: List<DaySummaryDto>
)

data class PeriodDto(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)

data class DaySummaryDto(
    @SerializedName("date") val date: String,
    @SerializedName("displayDate") val displayDate: String,
    @SerializedName("totalIronAbsorbed") val totalIronAbsorbed: Double,
    @SerializedName("hasInhibitor") val hasInhibitor: Boolean,
    @SerializedName("inhibitorCount") val inhibitorCount: Int,
    @SerializedName("totalFoodEntries") val totalFoodEntries: Int
)

// ═══════════════════════════════════════════════════════════════════════════
// ALIMENTOS POR CATEGORÍA (GET /foods/category/{category})
// ═══════════════════════════════════════════════════════════════════════════

data class CategoryFoodResponse(
    @SerializedName("category") val category: String,
    @SerializedName("items") val items: List<FoodItemSummaryDto>
)
//QUEDA
data class FoodItemSummaryDto(
    @SerializedName("foodItemId") val foodItemId: String,
    @SerializedName("name") val name: String,
    @SerializedName("ironType") val ironType: String,
    @SerializedName("ironMgPer100g") val ironMgPer100g: Double,
    @SerializedName("isInhibitor") val isInhibitor: Boolean,
    @SerializedName("warningMessage") val warningMessage: String?,
    @SerializedName("defaultUnit") val defaultUnit: String
)

// ═══════════════════════════════════════════════════════════════════════════
// BÚSQUEDA DE ALIMENTOS (GET /foods/search)
// ═══════════════════════════════════════════════════════════════════════════
//QUEDA
data class SearchFoodResponse(
    @SerializedName("searchText") val searchText: String,
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("items") val items: List<FoodItemSummaryDto>
)

// ═══════════════════════════════════════════════════════════════════════════
// DETALLES DE ALIMENTO (GET /foods/{foodItemId})
// ═══════════════════════════════════════════════════════════════════════════
//QUEDA
data class FoodItemDetailsResponse(
    @SerializedName("foodItemId") val foodItemId: String,
    @SerializedName("name") val name: String,
    @SerializedName("ironType") val ironType: String,
    @SerializedName("ironMgPer100g") val ironMgPer100g: Double,
    @SerializedName("isInhibitor") val isInhibitor: Boolean,
    @SerializedName("warningMessage") val warningMessage: String?,
    @SerializedName("defaultUnit") val defaultUnit: String
)
