package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.MyPatientsResponse
import pe.edu.upc.ferovafamily.data.remote.dto.PatientResponse
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterPatientRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PatientApiService {

    @POST("api/patients/register")
    suspend fun registerPatient(@Body request: RegisterPatientRequest): Response<PatientResponse>

    @GET("api/patients/mother/{motherId}")
    suspend fun getPatientsByMother(@Path("motherId") motherId: String): Response<List<PatientResponse>>

    @GET("api/patients/my-patients")
    suspend fun getMotherPatients(): Response<MyPatientsResponse>
}