package pe.edu.upc.ferovafamily.data.repository

import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.CreateMedicalRecordRequest
import pe.edu.upc.ferovafamily.data.remote.dto.HemoglobinControlRequest
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterPatientRequest
import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
import pe.edu.upc.ferovafamily.domain.model.MedicalRecord
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.repository.PatientRepository
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
        return try {
            val response = service.getHemoglobinEvolution(patientId)
            if (response.isSuccessful) {
                val body = response.body()
                body?.chart?.map { point ->
                    HemoglobinRecord(
                        date = formatDate(point.date),  // ← Formatear fecha a "dd MMMM"
                        value = point.hemoglobinLevel?.toFloat() ?: 0f
                    )
                } ?: mockHemoglobin()
            } else mockHemoglobin()
        } catch (_: Exception) { mockHemoglobin() }
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

    /** NURSE only — 403 para rol Mother */
    override suspend fun createMedicalRecord(patientId: String, initialHemoglobin: Double, diagnosis: String): MedicalRecord {
        val response = service.createMedicalRecord(
            CreateMedicalRecordRequest(patientId, initialHemoglobin, diagnosis)
        )
        return if (response.isSuccessful) {
            MedicalRecord(
                id = response.body()?.id ?: java.util.UUID.randomUUID().toString(),
                patientId = patientId, initialHemoglobin = initialHemoglobin, diagnosis = diagnosis
            )
        } else throw Exception("createMedicalRecord HTTP ${response.code()}")
    }

    /** NURSE only — 403 para rol Mother */
    override suspend fun registerHemoglobinControl(patientId: String, hemoglobinLevel: Double, date: String): HemoglobinRecord {
        val response = service.registerHemoglobinControl(
            HemoglobinControlRequest(patientId, hemoglobinLevel, date)
        )
        return if (response.isSuccessful) {
            HemoglobinRecord(date = date, value = hemoglobinLevel.toFloat())
        } else throw Exception("registerHemoglobinControl HTTP ${response.code()}")
    }

    private fun mockHemoglobin() = listOf(
        HemoglobinRecord("12 Abril",  7.0f),
        HemoglobinRecord("14 Mayo",   8.0f),
        HemoglobinRecord("12 Junio",  9.0f),
        HemoglobinRecord("28 Julio", 11.2f)
    )
}
