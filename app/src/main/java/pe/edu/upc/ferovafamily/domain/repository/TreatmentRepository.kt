package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.model.Treatment

interface TreatmentRepository {
    suspend fun getTodayDose(patientId: String): TodayDose
    suspend fun getDoseHistory(patientId: String): List<DoseRecord>
    suspend fun confirmDose(patientId: String): DoseRecord
    /** NURSE only */
    suspend fun startTreatment(patientId: String, durationDays: Int, ironDoseMg: Double): Treatment
}
