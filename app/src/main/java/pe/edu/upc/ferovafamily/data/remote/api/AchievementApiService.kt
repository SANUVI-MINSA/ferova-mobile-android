package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.AchievementProgressDto
import pe.edu.upc.ferovafamily.data.remote.dto.BadgesResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AchievementApiService {

    @GET("api/achievements-rewards/patients/{patientId}/achievement")
    suspend fun getAchievementProgress(
        @Path("patientId") patientId: String
    ): Response<AchievementProgressDto>

    @GET("api/achievements-rewards/patients/{patientId}/badges")
    suspend fun getBadges(
        @Path("patientId") patientId: String
    ): Response<BadgesResponseDto>
}
