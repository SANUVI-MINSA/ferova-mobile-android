package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.ChatResponse
import pe.edu.upc.ferovafamily.data.remote.dto.CloseConsultationRequest
import pe.edu.upc.ferovafamily.data.remote.dto.ConsultationResponse
import pe.edu.upc.ferovafamily.data.remote.dto.NurseInfoResponse
import pe.edu.upc.ferovafamily.data.remote.dto.PatientResponse
import pe.edu.upc.ferovafamily.data.remote.dto.SendMessageRequest
import pe.edu.upc.ferovafamily.data.remote.dto.StartConsultationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConsultationApiService {

    @POST("api/communication/consultations")
    suspend fun startConsultation(@Body request: StartConsultationRequest): Response<ConsultationResponse>

    @POST("api/communication/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ConsultationResponse>

    @GET("api/communication/patients/{motherId}")
    suspend fun getPatientsByMother(@Path("motherId") motherId: String): Response<List<PatientResponse>>

    @GET("api/communication/nurse-info/{patientId}")
    suspend fun getNurseInfo(@Path("patientId") patientId: String): Response<NurseInfoResponse>

    @GET("api/communication/chat/{consultationId}")
    suspend fun getChat(
        @Path("consultationId") consultationId: String,
        @Query("requesterId") requesterId: String
    ): Response<ChatResponse>

    @GET("api/communication/mother/{motherId}/consultations")
    suspend fun getMotherConsultations(@Path("motherId") motherId: String): Response<List<ConsultationResponse>>
}
