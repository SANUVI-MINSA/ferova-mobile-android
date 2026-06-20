package pe.edu.upc.ferovafamily.domain.model.nutrition

data class FoodItem(
    val foodItemId: String,
    val name: String,
    val ironType: String,
    val ironMgPer100g: Double,
    val isInhibitor: Boolean
)

data class FoodItemDetails(
    val foodItemId: String,
    val name: String,
    val ironType: String,
    val ironMgPer100g: Double,
    val isInhibitor: Boolean,
    val warningMessage: String?,
    val defaultUnit: String
)

data class SearchFoodResult(
    val searchText: String,
    val resultCount: Int,
    val items: List<FoodItem>
)

data class CategoryFood(
    val category: String,
    val items: List<FoodItem>
)

