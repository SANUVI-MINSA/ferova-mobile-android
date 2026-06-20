package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class ConfirmDoseRequest(
    @SerializedName("patientId")   val patientId: String,
    @SerializedName("confirmedAt") val confirmedAt: String    // ISO 8601
)


// ── Responses ──────────────────────────────────────────────────────────────────

data class TodayDoseDto(
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("treatmentId") val treatmentId: String?,
    @SerializedName("dailyDoseId") val dailyDoseId: String?,
    @SerializedName("scheduledDate") val scheduledDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("canConfirm") val canConfirm: Boolean?,
    @SerializedName("dosingHours") val dosingHours: String?,
    @SerializedName("message") val message: String?
)
data class DoseRecordDto(
    @SerializedName("id")                       val id: String?,
    @SerializedName("treatmentId")              val treatmentId: String?,      // ← NUEVO
    @SerializedName("scheduledDate")            val scheduledDate: String?,    // ← CAMBIA: "date" → "scheduledDate"
    @SerializedName("confirmedAt")              val confirmedAt: String?,
    @SerializedName("status")                   val status: String?,
    @SerializedName("hoursWithoutConfirmation") val hoursWithoutConfirmation: Int?  // ← NUEVO
)

data class DoseHistoryResponseDto(
    @SerializedName("patientId")      val patientId: String?,
    @SerializedName("patientName")    val patientName: String?,
    @SerializedName("supplementName") val supplementName: String?,
    @SerializedName("quantity")       val quantity: String?,
    @SerializedName("dosingHours")    val dosingHours: String?,
    @SerializedName("doses")          val doses: List<DoseRecordDto>?
)