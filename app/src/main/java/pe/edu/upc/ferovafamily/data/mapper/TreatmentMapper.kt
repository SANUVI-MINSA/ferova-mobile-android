package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.DoseRecordDto
import pe.edu.upc.ferovafamily.data.remote.dto.TodayDoseDto
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import java.util.UUID

fun TodayDoseDto.toDomain(patientId: String): TodayDose = TodayDose(
    patientId = patientId,
    canConfirm = canConfirm ?: true,
    scheduledTime = scheduledTime ?: "08:00 AM",
    confirmedAt = confirmedAt
)

fun DoseRecordDto.toDomain(): DoseRecord = DoseRecord(
    id = id ?: UUID.randomUUID().toString(),
    treatmentId = treatmentId ?: "",           // ← NUEVO
    scheduledDate = scheduledDate ?: "",       // ← CAMBIA: "date" → "scheduledDate"
    confirmedAt = confirmedAt,
    status = status ?: "PENDING",
    hoursWithoutConfirmation = hoursWithoutConfirmation ?: 0
)