package pe.edu.upc.ferovafamily.data.repository

import android.content.Context
import android.util.Log
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.ConsultationApiService
import pe.edu.upc.ferovafamily.data.remote.dto.SendMessageRequest
import pe.edu.upc.ferovafamily.data.remote.dto.StartConsultationRequest
import pe.edu.upc.ferovafamily.domain.model.communication.Consultation
import pe.edu.upc.ferovafamily.domain.model.communication.Message
import pe.edu.upc.ferovafamily.domain.model.communication.Nurse
import pe.edu.upc.ferovafamily.domain.model.communication.PatientWithNurse
import pe.edu.upc.ferovafamily.domain.repository.ConsultationRepository

private const val TAG = "ConsultationRepo"

class ConsultationRepositoryImpl(
    private val context: Context,
    private val service: ConsultationApiService
) : ConsultationRepository {

    private val tokenManager = TokenManager.getInstance(context)

    override suspend fun getPatientsWithNurse(): List<PatientWithNurse> {
        val response = service.getPatientsWithNurse()
        return if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            Log.e(TAG, "getPatientsWithNurse failed: ${response.code()}")
            emptyList()
        }
    }

    override suspend fun getMotherConsultations(): List<Consultation> {
        val response = service.getMotherConsultations()
        Log.d(TAG, "getMotherConsultations: code=${response.code()}")

        return if (response.isSuccessful) {
            val body = response.body()
            Log.d(TAG, "getMotherConsultations: body size=${body?.size ?: 0}")

            body?.mapNotNull {
                try {
                    it.toDomain()
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping consultation", e)
                    null
                }
            } ?: emptyList()
        } else {
            Log.e(TAG, "getMotherConsultations failed: ${response.code()}")
            emptyList()
        }
    }

    override suspend fun getNurseInfo(patientId: String): Nurse? {
        val response = service.getNurseInfo(patientId)
        return if (response.isSuccessful) response.body()?.toDomain() else null
    }

    override suspend fun startConsultation(patientId: String, firstMessage: String): Consultation {
        // ✅ CAMBIO: Usar userId en lugar de motherId
        val motherId = tokenManager.userId
            ?: throw Exception("Usuario no autenticado")

        Log.d(TAG, "startConsultation: userId=$motherId, patientId=$patientId")

        val response = service.startConsultation(
            StartConsultationRequest(
                motherId = motherId,
                patientId = patientId,
                firstMessageContent = firstMessage
            )
        )

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.toDomain()
                ?: throw Exception("La consulta creada no tiene ID válido")
        }

        val errorBody = response.errorBody()?.string() ?: "Sin detalles"
        Log.e(TAG, "startConsultation failed: ${response.code()} - $errorBody")
        throw Exception("No se pudo iniciar la consulta: ${response.code()} - $errorBody")
    }

    override suspend fun sendMessage(consultationId: String, content: String) {
        Log.d(TAG, "sendMessage: consultationId=$consultationId, content=${content.take(50)}...")

        val response = service.sendMessage(
            SendMessageRequest(consultationId = consultationId, content = content)
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "sendMessage failed: ${response.code()} - $errorBody")
            throw Exception("No se pudo enviar el mensaje: ${response.code()} - $errorBody")
        }

        Log.d(TAG, "sendMessage: Mensaje enviado correctamente")
    }

    override suspend fun getChat(consultationId: String): List<Message> {
        val response = service.getChat(consultationId)
        return if (response.isSuccessful) {
            response.body()?.messages?.map { it.toDomain() } ?: emptyList()
        } else {
            Log.e(TAG, "getChat failed: ${response.code()}")
            emptyList()
        }
    }
}