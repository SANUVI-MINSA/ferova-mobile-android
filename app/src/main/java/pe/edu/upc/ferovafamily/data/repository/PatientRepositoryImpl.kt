package pe.edu.upc.ferovafamily.data.repository

import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterPatientRequest
import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
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
                response.body()?.map { it.toDomain() } ?: mockHemoglobin()
            } else mockHemoglobin()
        } catch (_: Exception) { mockHemoglobin() }
    }

    private fun mockHemoglobin() = listOf(
        HemoglobinRecord("12 Abril",  7.0f),
        HemoglobinRecord("14 Mayo",   8.0f),
        HemoglobinRecord("12 Junio",  9.0f),
        HemoglobinRecord("28 Julio", 11.2f)
    )
}
