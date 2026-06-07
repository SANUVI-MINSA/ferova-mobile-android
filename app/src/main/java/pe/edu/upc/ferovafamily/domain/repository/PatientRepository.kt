package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
import pe.edu.upc.ferovafamily.domain.model.MedicalRecord
import pe.edu.upc.ferovafamily.domain.model.Patient

interface PatientRepository {
    suspend fun getMyPatients(): List<Patient>
    suspend fun registerPatient(patient: Patient): Boolean
    suspend fun getHemoglobinEvolution(patientId: String): List<HemoglobinRecord>
    /** NURSE only — crea expediente médico inicial */
    suspend fun createMedicalRecord(patientId: String, initialHemoglobin: Double, diagnosis: String): MedicalRecord
    /** NURSE only — registra control de hemoglobina */
    suspend fun registerHemoglobinControl(patientId: String, hemoglobinLevel: Double, date: String): HemoglobinRecord
}
