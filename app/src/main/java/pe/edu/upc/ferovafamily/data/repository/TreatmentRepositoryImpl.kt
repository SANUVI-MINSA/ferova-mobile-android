package pe.edu.upc.ferovafamily.data.repository

import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.remote.dto.ConfirmDoseRequest
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.repository.TreatmentRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TreatmentRepositoryImpl(
    private val service: TreatmentApiService
) : TreatmentRepository {

    override suspend fun getTodayDose(patientId: String): TodayDose {
        val response = service.getTodayDose(patientId)
        if (response.isSuccessful) {
            return response.body()?.toDomain() ?: mockTodayDose(patientId)
        }
        return mockTodayDose(patientId)
    }

    override suspend fun getDoseHistory(patientId: String): List<DoseRecord> {
        val response = service.getDoseHistory(patientId)
        if (response.isSuccessful) {
            return response.body()?.map { it.toDomain() } ?: mockDoseHistory(patientId)
        }
        return mockDoseHistory(patientId)
    }

    override suspend fun confirmDose(patientId: String): DoseRecord {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val response = service.confirmDose(ConfirmDoseRequest(patientId, now))
        if (response.isSuccessful) {
            return response.body()?.toDomain() ?: confirmedMockRecord(patientId, now)
        }
        return confirmedMockRecord(patientId, now)
    }

    // ── Mock data ─────────────────────────────────────────────────────────────

    private fun mockTodayDose(patientId: String) = TodayDose(
        patientId     = patientId,
        canConfirm    = true,
        scheduledTime = "08:00 AM",
        confirmedAt   = null
    )

    private fun mockDoseHistory(patientId: String) = listOf(
        DoseRecord(id = "d-1", patientId = patientId, date = "HOY",   confirmedAt = "08:15 AM", status = "CONFIRMED"),
        DoseRecord(id = "d-2", patientId = patientId, date = "AYER",  confirmedAt = null,        status = "OMITTED"),
        DoseRecord(id = "d-3", patientId = patientId, date = "AYER",  confirmedAt = "08:05 AM",  status = "CONFIRMED"),
        DoseRecord(id = "d-4", patientId = patientId, date = "15 ABR",confirmedAt = null,        status = "OMITTED"),
        DoseRecord(id = "d-5", patientId = patientId, date = "15 ABR",confirmedAt = "08:05 AM",  status = "CONFIRMED")
    )

    private fun confirmedMockRecord(patientId: String, confirmedAt: String) = DoseRecord(
        id          = java.util.UUID.randomUUID().toString(),
        patientId   = patientId,
        date        = "HOY",
        confirmedAt = confirmedAt,
        status      = "CONFIRMED"
    )
}
