package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Responses reales del backend ──────────────────────────────────────────────
//
// GET /api/achievements-rewards/patients/{id}/achievement →
// { patientId, patientName, status, totalPoints, currentStreak, longestStreak, message }
//
// GET /api/achievements-rewards/patients/{id}/badges →
// { patientId, patientName, badges: [...], message }

data class AchievementProgressDto(
    @SerializedName("patientId")     val patientId: String?,
    @SerializedName("patientName")   val patientName: String?,
    @SerializedName("status")        val status: String?,
    @SerializedName("totalPoints")   val totalPoints: Int?,
    @SerializedName("currentStreak") val currentStreak: Int?,
    @SerializedName("longestStreak") val longestStreak: Int?,
    @SerializedName("message")       val message: String?
)

data class BadgesResponseDto(
    @SerializedName("patientId")   val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("badges")      val badges: List<BadgeDto>?,
    @SerializedName("message")     val message: String?
)

data class BadgeDto(
    @SerializedName("id")              val id: String?,
    @SerializedName("name")            val name: String?,
    @SerializedName("description")     val description: String?,
    @SerializedName("isUnlocked")      val isUnlocked: Boolean?,
    @SerializedName("currentProgress") val currentProgress: Int?,
    @SerializedName("targetProgress")  val targetProgress: Int?,
    @SerializedName("unlockedAt")      val unlockedAt: String?,
    @SerializedName("category")        val category: String?
)
