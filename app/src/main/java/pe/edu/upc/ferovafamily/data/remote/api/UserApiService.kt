package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.LoginRequest
import pe.edu.upc.ferovafamily.data.remote.dto.LoginResponse
import pe.edu.upc.ferovafamily.data.remote.dto.MessageResponse
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterMotherRequest
import pe.edu.upc.ferovafamily.data.remote.dto.RequestPasswordCodeRequest
import pe.edu.upc.ferovafamily.data.remote.dto.ResetPasswordRequest
import pe.edu.upc.ferovafamily.data.remote.dto.UserResponse
import pe.edu.upc.ferovafamily.data.remote.dto.VerifyPasswordCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/register/mother")
    suspend fun registerMother(@Body request: RegisterMotherRequest): Response<UserResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserResponse>

    @POST("api/users/password/request-code")
    suspend fun requestPasswordCode(@Body request: RequestPasswordCodeRequest): Response<MessageResponse>

    @POST("api/users/password/verify-code")
    suspend fun verifyPasswordCode(@Body request: VerifyPasswordCodeRequest): Response<MessageResponse>

    @POST("api/users/password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>
}
