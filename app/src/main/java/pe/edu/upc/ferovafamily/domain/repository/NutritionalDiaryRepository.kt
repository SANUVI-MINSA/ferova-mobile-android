package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.nutrition.CategoryFood
import pe.edu.upc.ferovafamily.domain.model.nutrition.FoodItemDetails
import pe.edu.upc.ferovafamily.domain.model.nutrition.NutritionalHistory
import pe.edu.upc.ferovafamily.domain.model.nutrition.RegisterFoodEntryResult
import pe.edu.upc.ferovafamily.domain.model.nutrition.SearchFoodResult
import pe.edu.upc.ferovafamily.domain.model.nutrition.TodayDiary

interface NutritionalDiaryRepository {
    /**
     * Registra el consumo de un alimento
     */
    suspend fun registerFoodEntry(
        patientId: String,
        foodItemId: String,
        quantity: Int
    ): RegisterFoodEntryResult
    /**
     * Obtiene el diario nutricional del día actual
     */
    suspend fun getTodayDiary(patientId: String): TodayDiary
    /**
     * Obtiene alimentos por categoría
     */
    suspend fun getFoodsByCategory(category: String): CategoryFood
    /**
     * Busca alimentos por nombre
     */
    suspend fun searchFoods(text: String): SearchFoodResult
    /**
     * Obtiene detalles de un alimento
     */
    suspend fun getFoodDetail(foodItemId: String): FoodItemDetails
    /**
     * Obtiene el historial nutricional
     */
    suspend fun getNutritionalHistory(
        patientId: String,
        startDate: String? = null,
        endDate: String? = null
    ): NutritionalHistory
}
