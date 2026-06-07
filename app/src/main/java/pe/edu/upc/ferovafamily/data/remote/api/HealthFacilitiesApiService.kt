package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.AppointmentResponse
import pe.edu.upc.ferovafamily.data.remote.dto.AvailableSlotsResponse
import pe.edu.upc.ferovafamily.data.remote.dto.BookAppointmentRequest
import pe.edu.upc.ferovafamily.data.remote.dto.BookAppointmentResponse
import pe.edu.upc.ferovafamily.data.remote.dto.CancelAppointmentRequest
import pe.edu.upc.ferovafamily.data.remote.dto.HealthFacilityResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HealthFacilitiesApiService {

    @GET("api/health-facilities/nearby")
    suspend fun getNearbyFacilities(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<HealthFacilityResponse>>

    @GET("api/health-facilities/{id}")
    suspend fun getFacilityDetail(@Path("id") id: String): Response<HealthFacilityResponse>

    @GET("api/health-facilities/{facilityId}/available-slots")
    suspend fun getAvailableSlots(
        @Path("facilityId") facilityId: String,
        @Query("date") date: String   // "2026-06-10"
    ): Response<List<AvailableSlotsResponse>>

    @POST("api/health-facilities/appointments")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Response<BookAppointmentResponse>

    @PUT("api/health-facilities/appointments/cancel")
    suspend fun cancelAppointment(@Body request: CancelAppointmentRequest): Response<AppointmentResponse>

    @GET("api/health-facilities/patient/{patientId}/appointments")
    suspend fun getPatientAppointments(@Path("patientId") patientId: String): Response<List<AppointmentResponse>>

    @GET("api/health-facilities/appointments/mother/next")
    suspend fun getMotherNextAppointment(): Response<AppointmentResponse>
}
