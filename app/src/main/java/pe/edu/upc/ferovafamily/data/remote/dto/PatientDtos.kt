package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

data class RegisterPatientRequest(
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("birthDate") val birthDate: String,   // "2023-05-10"
    @SerializedName("gender") val gender: String,          // "MALE" | "FEMALE"
    @SerializedName("weight") val weight: Double,
    @SerializedName("height") val height: Double,
    @SerializedName("motherId") val motherId: String       // ID de la madre autenticada
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class PatientResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("height") val height: Double?,
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("motherId") val motherId: String?
)
