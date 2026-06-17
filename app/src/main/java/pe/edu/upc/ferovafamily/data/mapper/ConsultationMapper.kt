package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.ConsultationResponse
import pe.edu.upc.ferovafamily.data.remote.dto.MessageDto
import pe.edu.upc.ferovafamily.data.remote.dto.NurseInfoResponse
import pe.edu.upc.ferovafamily.data.remote.dto.PatientWithNurseDto
import pe.edu.upc.ferovafamily.domain.model.communication.Consultation
import pe.edu.upc.ferovafamily.domain.model.communication.Message
import pe.edu.upc.ferovafamily.domain.model.communication.Nurse
import pe.edu.upc.ferovafamily.domain.model.communication.PatientWithNurse

// ── Patient + nurse ───────────────────────────────────────────────────────────

fun PatientWithNurseDto.toDomain(): PatientWithNurse = PatientWithNurse(
    patientId = patientId ?: "",
    patientName = patientName ?: "Paciente",
    hasNurse = hasNurse,
    nurse = if (hasNurse && !nurseId.isNullOrBlank()) {
        Nurse(
            id = nurseId,
            name = nurseName ?: "Enfermera asignada"
        )
    } else null
)

// ── Nurse info ────────────────────────────────────────────────────────────────

fun NurseInfoResponse.toDomain(): Nurse? =
    if (hasNurse && !nurseId.isNullOrBlank()) {
        Nurse(
            id = nurseId,
            name = nurseName ?: "Enfermera asignada",
            specialty = specialty ?: "Enfermería pediátrica",
            email = nurseEmail ?: ""
        )
    } else null

// ── Consultation + messages ───────────────────────────────────────────────────

fun ConsultationResponse.toDomain(): Consultation = Consultation(
    id = id,
    patientId = patientId ?: "",
    patientName = patientName ?: "Paciente",
    nurse = Nurse(
        id = nurseId ?: "",
        name = nurseName ?: "Enfermera",
        specialty = "Enfermería pediátrica"
    ),
    isOpen = status == "OPEN",
    messages = messages?.map { it.toDomain() } ?: emptyList()
)

fun MessageDto.toDomain(): Message = Message(
    id = id,
    text = content,
    isFromNurse = senderRole == "NURSE",
    time = timestamp.toHourMinute()
)

// ── Helpers ───────────────────────────────────────────────────────────────────

/** Convierte un timestamp ISO ("2026-06-17T13:42:00Z") a "HH:mm". */
private fun String?.toHourMinute(): String {
    if (this.isNullOrBlank()) return ""
    val t = substringAfter('T', "")
    return if (t.length >= 5) t.substring(0, 5) else takeLast(5)
}
