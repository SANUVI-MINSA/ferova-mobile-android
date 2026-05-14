package pe.edu.upc.ferovafamily.data.remote.api

import pe.edu.upc.ferovafamily.data.remote.dto.DailyDiaryResponse
import pe.edu.upc.ferovafamily.data.remote.dto.FoodEntryResponse
import pe.edu.upc.ferovafamily.data.remote.dto.FoodItemResponse
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterFoodEntryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionalDiaryApiService {

    @POST("api/nutritional-diary/food-entry")
    suspend fun registerFoodEntry(@Body request: RegisterFoodEntryRequest): Response<FoodEntryResponse>

    @GET("api/nutritional-diary/today/{patientId}")
    suspend fun getTodayDiary(@Path("patientId") patientId: String): Response<DailyDiaryResponse>

    @GET("api/nutritional-diary/foods/category/{category}")
    suspend fun getFoodsByCategory(@Path("category") category: String): Response<List<FoodItemResponse>>

    @GET("api/nutritional-diary/foods/search")
    suspend fun searchFoods(@Query("text") text: String): Response<List<FoodItemResponse>>

    @GET("api/nutritional-diary/foods/{foodItemId}")
    suspend fun getFoodDetail(@Path("foodItemId") foodItemId: String): Response<FoodItemResponse>

    @GET("api/nutritional-diary/history/{patientId}")
    suspend fun getNutritionalHistory(@Path("patientId") patientId: String): Response<List<DailyDiaryResponse>>
}
