package pe.edu.upc.ferovafamily.data.repository

import android.util.Log
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.NutritionalDiaryApiService
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterFoodEntryRequest
import pe.edu.upc.ferovafamily.domain.model.nutrition.*
import pe.edu.upc.ferovafamily.domain.repository.NutritionalDiaryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val TAG = "NutritionalDiaryRepo"

class NutritionalDiaryRepositoryImpl(
    private val service: NutritionalDiaryApiService
) : NutritionalDiaryRepository {

    override suspend fun getTodayDiary(patientId: String): TodayDiary {
        // ✅ Obtener fecha actual en UTC para evitar problemas de zona horaria
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())

        Log.d(TAG, "========================================")
        Log.d(TAG, "📅 getTodayDiary - INICIO")
        Log.d(TAG, "📅 patientId: $patientId")
        Log.d(TAG, "📅 fecha UTC: $todayDate")
        Log.d(TAG, "========================================")

        val response = try {
            service.getTodayDiary(patientId, todayDate)  // ✅ Enviar fecha UTC
        } catch (e: Exception) {
            Log.e(TAG, "❌ getTodayDiary - Error de conexión: ${e.message}", e)
            throw Exception("Fallo de conexión: ${e.message}")
        }

        Log.d(TAG, "📥 Response code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            Log.d(TAG, "📥 Body: $body")
            Log.d(TAG, "✅ getTodayDiary - ÉXITO")
            Log.d(TAG, "========================================")
            return body?.toDomain() ?: throw Exception("Cuerpo de respuesta vacío")
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            Log.e(TAG, "❌ getTodayDiary - ERROR ${response.code()}: $errorBody")
            Log.d(TAG, "========================================")
            throw Exception("Error ${response.code()} al cargar diario para ID: $patientId. $errorBody")
        }
    }

    override suspend fun registerFoodEntry(
        patientId: String,
        foodItemId: String,
        quantity: Int
    ): RegisterFoodEntryResult {
        Log.d(TAG, "========================================")
        Log.d(TAG, "📝 registerFoodEntry - INICIO")
        Log.d(TAG, "📝 patientId: $patientId")
        Log.d(TAG, "📝 foodItemId: $foodItemId")
        Log.d(TAG, "📝 quantity: $quantity")
        Log.d(TAG, "========================================")

        val request = RegisterFoodEntryRequest(
            patientId,
            foodItemId,
            quantity
        )

        Log.d(TAG, "📤 Request: $request")

        val response = try {
            service.registerFoodEntry(request)
        } catch (e: Exception) {
            Log.e(TAG, "❌ registerFoodEntry - Excepción de red: ${e.message}", e)
            throw Exception("Error de red: ${e.message}")
        }

        Log.d(TAG, "📥 Response code: ${response.code()}")
        Log.d(TAG, "📥 Response body: ${response.body()}")

        if (response.isSuccessful) {
            val result = response.body()!!.toDomain()
            Log.d(TAG, "✅ registerFoodEntry - ÉXITO")
            Log.d(TAG, "   - success: ${result.success}")
            Log.d(TAG, "   - message: ${result.message}")
            Log.d(TAG, "   - newTotalIronAbsorbed: ${result.newTotalIronAbsorbed}")
            Log.d(TAG, "   - warningMessage: ${result.warningMessage}")
            Log.d(TAG, "========================================")
            return result
        } else {
            val errorBody = response.errorBody()?.string() ?: "Sin cuerpo de error"
            Log.e(TAG, "❌ registerFoodEntry - ERROR ${response.code()}")
            Log.e(TAG, "   - errorBody: $errorBody")
            Log.d(TAG, "========================================")
            throw Exception("Error ${response.code()} al registrar alimento: $errorBody")
        }
    }

    override suspend fun getNutritionalHistory(
        patientId: String,
        startDate: String?,
        endDate: String?
    ): NutritionalHistory {
        Log.d(TAG, "getNutritionalHistory - patientId: $patientId, startDate: $startDate, endDate: $endDate")
        val response = service.getNutritionalHistory(patientId, startDate, endDate)
        Log.d(TAG, "getNutritionalHistory - Response code: ${response.code()}")
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()} al cargar historial")
    }

    override suspend fun getFoodsByCategory(category: String): CategoryFood {
        Log.d(TAG, "getFoodsByCategory - category: $category")
        val response = service.getFoodsByCategory(category)
        Log.d(TAG, "getFoodsByCategory - Response code: ${response.code()}")
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }

    override suspend fun searchFoods(searchText: String): SearchFoodResult {
        Log.d(TAG, "searchFoods - searchText: $searchText")
        val response = service.searchFoods(searchText)
        Log.d(TAG, "searchFoods - Response code: ${response.code()}")
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }

    override suspend fun getFoodDetail(foodItemId: String): FoodItemDetails {
        Log.d(TAG, "getFoodDetail - foodItemId: $foodItemId")
        val response = service.getFoodItemDetail(foodItemId)
        Log.d(TAG, "getFoodDetail - Response code: ${response.code()}")
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }
}