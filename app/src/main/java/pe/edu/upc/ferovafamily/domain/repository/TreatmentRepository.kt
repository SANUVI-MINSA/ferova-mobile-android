package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.DoseHistory
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose

interface TreatmentRepository {
    suspend fun getTodayDose(patientId: String): TodayDose
    suspend fun getDoseHistory(patientId: String): DoseHistory
    suspend fun confirmDose(patientId: String): DoseRecord
}
