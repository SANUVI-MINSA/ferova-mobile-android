package pe.edu.upc.ferovafamily.data.repository

import android.util.Log
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterPatientRequest
import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.repository.PatientRepository

private const val TAG = "PatientRepo"

class PatientRepositoryImpl(
    private val service: PatientApiService
) : PatientRepository {

    override suspend fun getMyPatients(): List<Patient> {
        return try {
            val response = service.getMyPatients()
            if (response.isSuccessful) {
                // El backend devuelve { motherId, patients: [{id, name}] }
                response.body()?.patients?.map { it.toDomain() } ?: emptyList()
            } else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    override suspend fun registerPatient(patient: Patient): Boolean {
        return try {
            val response = service.registerPatient(
                RegisterPatientRequest(
                    name      = patient.name,
                    lastName  = patient.lastName,
                    birthDate = patient.birthDate,
                    gender    = patient.gender,
                    weight    = patient.weight,
                    height    = patient.height,
                    motherId  = patient.motherId ?: ""
                )
            )
            response.isSuccessful
        } catch (_: Exception) { false }
    }

    override suspend fun getHemoglobinEvolution(patientId: String): List<HemoglobinRecord> {
        Log.d(TAG, "getHemoglobinEvolution called with patientId: $patientId")
        return try {
            val response = service.getHemoglobinEvolution(patientId)
            Log.d(TAG, "Hemoglobin response code: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Hemoglobin response body: $body")

                val chart = body?.chart
                Log.d(TAG, "Chart size: ${chart?.size ?: 0}")

                chart?.forEach { point ->
                    Log.d(TAG, "Chart point: date=${point.date}, hemoglobinLevel=${point.hemoglobinLevel}")
                }

                body?.chart?.map { point ->
                    HemoglobinRecord(
                        date = formatDate(point.date),
                        value = point.hemoglobinLevel?.toFloat() ?: 0f
                    )
                } ?: emptyList()
            } else {
                Log.e(TAG, "Hemoglobin response not successful: ${response.code()}")
                emptyList()            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getHemoglobinEvolution", e)
            emptyList()
        }
    }

    private fun formatDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return ""
        return try {
            val parsed = java.time.LocalDate.parse(isoDate.substring(0, 10))
            "${parsed.dayOfMonth} ${parsed.month.getDisplayName(
                java.time.format.TextStyle.SHORT,
                java.util.Locale("es")
            )}"
        } catch (_: Exception) { isoDate }
    }


}
