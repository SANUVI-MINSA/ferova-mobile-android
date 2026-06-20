package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.communication.Consultation
import pe.edu.upc.ferovafamily.domain.model.communication.Message
import pe.edu.upc.ferovafamily.domain.model.communication.Nurse
import pe.edu.upc.ferovafamily.domain.model.communication.PatientWithNurse

/**
 * Contrato del bounded context de Communication Management (madre ↔ enfermera).
 * La identidad de la madre viaja en el JWT (AuthInterceptor), por eso ningún
 * método recibe motherId / senderId.
 */
interface ConsultationRepository {
    suspend fun getPatientsWithNurse(): List<PatientWithNurse>
    suspend fun getMotherConsultations(): List<Consultation>
    suspend fun getNurseInfo(patientId: String): Nurse?
    suspend fun startConsultation(patientId: String, firstMessage: String): Consultation
    suspend fun sendMessage(consultationId: String, content: String)
    suspend fun getChat(consultationId: String): List<Message>
}
