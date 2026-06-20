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
    @SerializedName("content") val content: String
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class PatientWithNurseDto(
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("hasNurseAssigned") val hasNurse: Boolean = false,
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("nurseName") val nurseName: String?
)

data class ConsultationResponse(
    @SerializedName("consultationId") val consultationId: String?,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("motherId") val motherId: String?,
    @SerializedName("motherName") val motherName: String?,
    @SerializedName("nurseId") val nurseId: String?,
    @SerializedName("nurseName") val nurseName: String?,
    @SerializedName("lastMessage") val lastMessage: String?,
    @SerializedName("lastMessageDate") val lastMessageDate: String?,
    @SerializedName("lastMessageSenderRole") val lastMessageSenderRole: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("messageCount") val messageCount: Int? = 0,
    @SerializedName("status") val status: String? = "OPEN",
    @SerializedName("messages") val messages: List<MessageDto>? = emptyList()
)

// ✅ CAMBIO: timestamp → sentAt
data class MessageDto(
    @SerializedName("id") val id: String?,
    @SerializedName("senderId") val senderId: String?,
    @SerializedName("senderRole") val senderRole: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("sentAt") val sentAt: String?  // ← CAMBIADO de "timestamp" a "sentAt"
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