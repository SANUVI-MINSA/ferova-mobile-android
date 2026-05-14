package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

data class RegisterFoodEntryRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("foodItemId") val foodItemId: String,
    @SerializedName("quantity") val quantity: Double
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class FoodItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String?,
    @SerializedName("ironPer100g") val ironPer100g: Double?,
    @SerializedName("absorptionRate") val absorptionRate: Double?,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class FoodEntryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("foodItem") val foodItem: FoodItemResponse?,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("ironAbsorbed") val ironAbsorbed: Double?,
    @SerializedName("registeredAt") val registeredAt: String?
)

data class DailyDiaryResponse(
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("entries") val entries: List<FoodEntryResponse>?,
    @SerializedName("totalIronAbsorbed") val totalIronAbsorbed: Double?
)
