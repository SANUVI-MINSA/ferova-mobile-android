package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

data class StartConsultationRequest(
    @SerializedName("motherId") val motherId: String,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("firstMessageContent") val firstMessageContent: String
)

data class SendMessageRequest(
    @SerializedName("consultationId") val consultationId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderRole") val senderRole: String,   // "MOTHER" | "NURSE"
    @SerializedName("content") val content: String
)

data class CloseConsultationRequest(
    @SerializedName("consultationId") val consultationId: String,
    @SerializedName("nurseId") val nurseId: String
)

// ── Responses ─────────────────────────────────────────────────────────────────

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
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("hasNurse") val hasNurse: Boolean = false
)

data class ChatResponse(
    @SerializedName("consultationId") val consultationId: String?,
    @SerializedName("messages") val messages: List<MessageDto>?
)
