package pe.edu.upc.ferovafamily.data.repository

import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.NutritionalDiaryApiService
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterFoodEntryRequest
import pe.edu.upc.ferovafamily.domain.model.nutrition.*
import pe.edu.upc.ferovafamily.domain.repository.NutritionalDiaryRepository

class NutritionalDiaryRepositoryImpl(
    private val service: NutritionalDiaryApiService
) : NutritionalDiaryRepository {

    override suspend fun getTodayDiary(patientId: String): TodayDiary {
        val response = try {
            service.getTodayDiary(patientId)
        } catch (e: Exception) {
            throw Exception("Fallo de conexión: ${e.message}")
        }

        if (response.isSuccessful) {
            return response.body()?.toDomain() ?: throw Exception("Cuerpo de respuesta vacío")
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            // Esto nos dirá exactamente qué está fallando (ej: 404 si el ID no existe)
            throw Exception("Error ${response.code()} al cargar diario para ID: $patientId. $errorBody")
        }
    }

    override suspend fun registerFoodEntry(
        patientId: String,
        foodItemId: String,
        quantity: Int
    ): RegisterFoodEntryResult {
        val request = RegisterFoodEntryRequest(
            patientId,
            foodItemId,
            quantity
        )
        val response = service.registerFoodEntry(request)
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()} al registrar alimento")
    }

    override suspend fun getNutritionalHistory(
        patientId: String,
        startDate: String?,
        endDate: String?
    ): NutritionalHistory {
        val response = service.getNutritionalHistory(patientId, startDate, endDate)
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()} al cargar historial")
    }

    override suspend fun getFoodsByCategory(category: String): CategoryFood {
        val response = service.getFoodsByCategory(category)
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }

    override suspend fun searchFoods(searchText: String): SearchFoodResult {
        val response = service.searchFoods(searchText)
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }

    override suspend fun getFoodDetail(foodItemId: String): FoodItemDetails {
        val response = service.getFoodItemDetail(foodItemId)
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Error ${response.code()}")
    }
}
