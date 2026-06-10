package pe.edu.upc.ferovafamily.data.repository

import android.util.Log
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.remote.dto.ConfirmDoseRequest
import pe.edu.upc.ferovafamily.domain.model.DoseHistory
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.repository.TreatmentRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "TreatmentRepo"

class TreatmentRepositoryImpl(
    private val service: TreatmentApiService
) : TreatmentRepository {

    override suspend fun getTodayDose(patientId: String): TodayDose {
        Log.d(TAG, "getTodayDose called for patient: $patientId")
        val response = service.getTodayDose(patientId)

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!

            //  Si no tiene tratamiento activo, lanzar excepción
            if (body.message == "Treatment has not started yet") {
                throw Exception("No active treatment")
            }

            return body.toDomain(patientId)
        } else {
            throw Exception("Failed to get today's dose: ${response.code()}")
        }
    }

    override suspend fun getDoseHistory(patientId: String): DoseHistory {
        Log.d(TAG, "========== getDoseHistory ==========")
        Log.d(TAG, "patientId: $patientId")

        val response = service.getDoseHistory(patientId)
        Log.d(TAG, "Response code: ${response.code()}")

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            Log.d(TAG, "Response body: $body")
            Log.d(TAG, "patientName: ${body.patientName}")
            Log.d(TAG, "supplementName: ${body.supplementName}")
            Log.d(TAG, "quantity: ${body.quantity}")
            Log.d(TAG, "dosingHours: ${body.dosingHours}")
            Log.d(TAG, "doses count: ${body.doses?.size ?: 0}")

            body.doses?.forEachIndexed { index, dose ->
                Log.d(TAG, "Dose $index: id=${dose.id}, status=${dose.status}, scheduledDate=${dose.scheduledDate}")
            }

            return DoseHistory(
                patientId = body.patientId ?: "",
                patientName = body.patientName ?: "",
                supplementName = body.supplementName ?: "",
                quantity = body.quantity ?: "",
                dosingHours = body.dosingHours ?: "",
                doses = body.doses?.map { doseDto ->
                    DoseRecord(
                        id = doseDto.id ?: "",
                        treatmentId = doseDto.treatmentId ?: "",
                        scheduledDate = doseDto.scheduledDate ?: "",
                        confirmedAt = doseDto.confirmedAt,
                        status = doseDto.status ?: "PENDING",
                        hoursWithoutConfirmation = doseDto.hoursWithoutConfirmation ?: 0
                    )
                } ?: emptyList()
            )
        } else {
            Log.e(TAG, "Failed to get dose history. Code: ${response.code()}, Message: ${response.message()}")
            throw Exception("Failed to get dose history: ${response.code()}")
        }
    }
    override suspend fun confirmDose(patientId: String): DoseRecord {
        Log.d(TAG, "confirmDose called for patient: $patientId")
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val response = service.confirmDose(ConfirmDoseRequest(patientId, now))

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            return DoseRecord(
                id = body.id ?: "",
                treatmentId = body.treatmentId ?: "",
                scheduledDate = body.scheduledDate ?: "",
                confirmedAt = body.confirmedAt,
                status = body.status ?: "",
                hoursWithoutConfirmation = body.hoursWithoutConfirmation ?: 0
            )
        } else {
            throw Exception("Failed to confirm dose: ${response.code()}")
        }
    }
}