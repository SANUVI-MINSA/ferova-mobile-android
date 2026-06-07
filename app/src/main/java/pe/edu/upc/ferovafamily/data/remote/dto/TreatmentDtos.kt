package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class ConfirmDoseRequest(
    @SerializedName("patientId")   val patientId: String,
    @SerializedName("confirmedAt") val confirmedAt: String    // ISO 8601
)

// POST /api/treatment-tracking/treatments  (NURSE only)
data class StartTreatmentRequest(
    @SerializedName("patientId")    val patientId: String,
    @SerializedName("startDate")    val startDate: String,     // "yyyy-MM-dd"
    @SerializedName("durationDays") val durationDays: Int,     // 90
    @SerializedName("ironDoseMg")   val ironDoseMg: Double     // mg diarios
)

// ── Responses ──────────────────────────────────────────────────────────────────
//
// GET /api/treatment-tracking/patients/{id}/today-dose →
// { canConfirm: bool, message: string }
//
// GET /api/treatment-tracking/patients/{id}/dose-history →
// [ { id, patientId, date, confirmedAt, status } ]
//
// POST /api/treatment-tracking/doses/confirm →
// { id, patientId, date, confirmedAt, status }

data class TodayDoseDto(
    @SerializedName("canConfirm")    val canConfirm: Boolean?,
    @SerializedName("scheduledTime") val scheduledTime: String?,   // puede o no estar
    @SerializedName("confirmedAt")   val confirmedAt: String?,
    @SerializedName("message")       val message: String?
)

data class DoseRecordDto(
    @SerializedName("id")          val id: String?,
    @SerializedName("patientId")   val patientId: String?,
    @SerializedName("date")        val date: String?,
    @SerializedName("confirmedAt") val confirmedAt: String?,
    @SerializedName("status")      val status: String?        // "CONFIRMED" | "OMITTED"
)
