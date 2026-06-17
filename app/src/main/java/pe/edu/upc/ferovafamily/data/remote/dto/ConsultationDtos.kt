package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────
// El backend deriva motherId / senderId / senderRole del JWT, por eso no van en el body.

data class StartConsultationRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("firstMessageContent") val firstMessageContent: String
)

data class SendMessageRequest(
    @SerializedName("consultationId") val consultationId: String,
    @SerializedName("content") val content: String
)

// ── Responses ─────────────────────────────────────────────────────────────────

/**
 * GET /api/communication/patients — paciente + estado de asignación de enfermera.
 * Campos verificados contra la respuesta real del backend.
 */
data class PatientWithNurseDto(
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("hasNurseAssigned") val hasNurse: Boolean = false,
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("nurseName") val nurseName: String?
)

data class ConsultationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("motherId") val motherId: String?,
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("nurseName") val nurseName: String?,
    @SerializedName("status") val status: String?,   // "OPEN" | "CLOSED"
    @SerializedName("messages") val messages: List<MessageDto>?
)

data class MessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("senderId") val senderId: String?,
    @SerializedName("senderRole") val senderRole: String?,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: String?
)

data class NurseInfoResponse(
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("nurseName") val nurseName: String?,
    @SerializedName("nurseEmail") val nurseEmail: String?,
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("hasNurse") val hasNurse: Boolean = false
)

data class ChatResponse(
    @SerializedName("consultationId") val consultationId: String?,
    @SerializedName("messages") val messages: List<MessageDto>?
)
