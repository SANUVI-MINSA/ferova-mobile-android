package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

data class BookAppointmentRequest(
    @SerializedName("facilityId") val facilityId: String,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("appointmentDate") val appointmentDate: String,  // "2026-06-10"
    @SerializedName("appointmentTime") val appointmentTime: String   // "09:00"
)

data class CancelAppointmentRequest(
    @SerializedName("appointmentId") val appointmentId: String
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class HealthFacilityResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("services") val services: List<String>?,
    @SerializedName("availableDays") val availableDays: List<String>?,
    @SerializedName("distanceKm") val distanceKm: Double?,
    @SerializedName("status") val status: String?
)

data class AppointmentResponse(
    @SerializedName("id") val id: String,
    @SerializedName("facilityId") val facilityId: String?,
    @SerializedName("facilityName") val facilityName: String?,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("appointmentDate") val appointmentDate: String?,
    @SerializedName("appointmentTime") val appointmentTime: String?,
    @SerializedName("status") val status: String?
)

data class AvailableSlotsResponse(
    @SerializedName("slots") val slots: List<String>?
)
