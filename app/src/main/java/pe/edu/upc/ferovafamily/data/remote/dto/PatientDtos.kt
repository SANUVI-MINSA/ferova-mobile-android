package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

// POST /api/patients/medical-record  (NURSE only)
data class CreateMedicalRecordRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("initialHemoglobin") val initialHemoglobin: Double,
    @SerializedName("diagnosis") val diagnosis: String,
    @SerializedName("notes") val notes: String? = null
)

// POST /api/patients/hemoglobin-control  (NURSE only)
data class HemoglobinControlRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("hemoglobinLevel") val hemoglobinLevel: Double,
    @SerializedName("controlDate") val controlDate: String     // "yyyy-MM-dd"
)

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

data class HemoglobinRecordDto(
    @SerializedName("date") val date: String?,
    @SerializedName("value") val value: Float?,
    @SerializedName("unit") val unit: String?
)

data class PatientResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String = "",
    @SerializedName("birthDate") val birthDate: String? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("weight") val weight: Double? = null,
    @SerializedName("height") val height: Double? = null,
    @SerializedName("nurseId") val nurseId: String? = null,
    @SerializedName("motherId") val motherId: String? = null
)

// Respuesta real de GET /api/patients/my-patients:
// { "motherId": "...", "patients": [{ "id": "...", "name": "..." }] }
data class MyPatientsResponseDto(
    @SerializedName("motherId") val motherId: String?,
    @SerializedName("patients") val patients: List<PatientResponse>?
)