package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.DoseRecordDto
import pe.edu.upc.ferovafamily.data.remote.dto.TodayDoseDto
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose

fun TodayDoseDto.toDomain(): TodayDose {
    val patientId = null
    return TodayDose(
        patientId = patientId ?: "",
        canConfirm = canConfirm ?: true,
        scheduledTime = scheduledTime ?: "08:00 AM",
        confirmedAt = confirmedAt
    )
}

fun DoseRecordDto.toDomain(): DoseRecord = DoseRecord(
    id          = id          ?: java.util.UUID.randomUUID().toString(),
    patientId   = patientId   ?: "",
    date        = date        ?: "",
    confirmedAt = confirmedAt,
    status      = status      ?: "OMITTED"
)
