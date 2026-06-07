package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.ConfirmDoseRequest
import pe.edu.upc.ferovafamily.data.remote.dto.DoseRecordDto
import pe.edu.upc.ferovafamily.data.remote.dto.StartTreatmentRequest
import pe.edu.upc.ferovafamily.data.remote.dto.TodayDoseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TreatmentApiService {

    /** NURSE only — inicia un tratamiento para un paciente */
    @POST("api/treatment-tracking/treatments")
    suspend fun startTreatment(
        @Body request: StartTreatmentRequest
    ): Response<DoseRecordDto>

    @GET("api/treatment-tracking/patients/{patientId}/today-dose")
    suspend fun getTodayDose(
        @Path("patientId") patientId: String
    ): Response<TodayDoseDto>

    @GET("api/treatment-tracking/patients/{patientId}/dose-history")
    suspend fun getDoseHistory(
        @Path("patientId") patientId: String
    ): Response<List<DoseRecordDto>>

    @POST("api/treatment-tracking/doses/confirm")
    suspend fun confirmDose(
        @Body request: ConfirmDoseRequest
    ): Response<DoseRecordDto>
}
