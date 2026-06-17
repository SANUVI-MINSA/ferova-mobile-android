package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.ChatResponse
import pe.edu.upc.ferovafamily.data.remote.dto.ConsultationResponse
import pe.edu.upc.ferovafamily.data.remote.dto.NurseInfoResponse
import pe.edu.upc.ferovafamily.data.remote.dto.PatientWithNurseDto
import pe.edu.upc.ferovafamily.data.remote.dto.SendMessageRequest
import pe.edu.upc.ferovafamily.data.remote.dto.StartConsultationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints del módulo communication. Paths verificados contra el Swagger real
 * (/api-docs/). La madre se identifica por el JWT, por eso patients y
 * consultations/mother no llevan parámetros de ruta.
 */
interface ConsultationApiService {

    @POST("api/communication/consultations")
    suspend fun startConsultation(@Body request: StartConsultationRequest): Response<ConsultationResponse>

    @POST("api/communication/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ConsultationResponse>

    @GET("api/communication/patients")
    suspend fun getPatientsWithNurse(): Response<List<PatientWithNurseDto>>

    @GET("api/communication/nurse-info/{patientId}")
    suspend fun getNurseInfo(@Path("patientId") patientId: String): Response<NurseInfoResponse>

    @GET("api/communication/chat/{consultationId}")
    suspend fun getChat(@Path("consultationId") consultationId: String): Response<ChatResponse>

    @GET("api/communication/consultations/mother")
    suspend fun getMotherConsultations(): Response<List<ConsultationResponse>>
}
