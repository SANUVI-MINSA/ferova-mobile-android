package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.CategoryFoodResponse
import pe.edu.upc.ferovafamily.data.remote.dto.FoodEntryResponse
import pe.edu.upc.ferovafamily.data.remote.dto.FoodItemDetailsResponse
import pe.edu.upc.ferovafamily.data.remote.dto.NutritionalHistoryResponse
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterFoodEntryRequest
import pe.edu.upc.ferovafamily.data.remote.dto.SearchFoodResponse
import pe.edu.upc.ferovafamily.data.remote.dto.TodayDiaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionalDiaryApiService {

    /**
     * POST /food-entry
     * Registra el consumo de un alimento
     */
    @POST("api/nutritional-diary/food-entry")
    suspend fun registerFoodEntry(
        @Body request: RegisterFoodEntryRequest
    ): Response<FoodEntryResponse>

    /**
     * GET /today/{patientId}
     * Obtiene el diario nutricional del día actual
     * ✅ MODIFICADO: Acepta fecha opcional para evitar problemas de zona horaria
     */
    @GET("api/nutritional-diary/today/{patientId}")
    suspend fun getTodayDiary(
        @Path("patientId") patientId: String,
        @Query("date") date: String? = null  // ✅ Fecha opcional en formato yyyy-MM-dd
    ): Response<TodayDiaryResponse>

    /**
     * GET /foods/category/{category}
     * Obtiene alimentos filtrados por categoría
     */
    @GET("api/nutritional-diary/foods/category/{category}")
    suspend fun getFoodsByCategory(
        @Path("category") category: String
    ): Response<CategoryFoodResponse>

    /**
     * GET /foods/search
     * Busca alimentos por nombre
     */
    @GET("api/nutritional-diary/foods/search")
    suspend fun searchFoods(
        @Query("text") text: String
    ): Response<SearchFoodResponse>

    /**
     * GET /foods/{foodItemId}
     * Obtiene detalles de un alimento específico
     */
    @GET("api/nutritional-diary/foods/{foodItemId}")
    suspend fun getFoodItemDetail(
        @Path("foodItemId") foodItemId: String
    ): Response<FoodItemDetailsResponse>

    /**
     * GET /history/{patientId}
     * Obtiene el historial nutricional (últimos 30 días por defecto)
     */
    @GET("api/nutritional-diary/history/{patientId}")
    suspend fun getNutritionalHistory(
        @Path("patientId") patientId: String,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): Response<NutritionalHistoryResponse>
}