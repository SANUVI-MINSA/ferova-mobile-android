package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
import pe.edu.upc.ferovafamily.domain.model.Patient

interface PatientRepository {
    suspend fun getMyPatients(): List<Patient>
    suspend fun registerPatient(patient: Patient): Boolean
    suspend fun getHemoglobinEvolution(patientId: String): List<HemoglobinRecord>

}
