package pe.edu.upc.ferovafamily.presentation.nutritional_diary.model

data class FoodItem(
    val id: Int,
    val name: String,
    val nutrientContent: Pair<Int, String>,//ironMg, Hemo/No-Hemo
    val isInhibitor: Boolean,
    val category: String
)

data class FoodEntry(
    val foodItemId: Int,
    val patientName: String,
    var quantity: Int,
    val unit: String,
    var ironContributed: Double,
    val registeredAt: String
)

object FoodDatabase {
    val foodItems = listOf(
        FoodItem(
            id = 1,
            name = "Hígado de res",
            nutrientContent = Pair(6, "Hemo"),
            isInhibitor = false,
            category = "MEAT"
        ),
        FoodItem(
            id = 2,
            name = "Carne de res",
            nutrientContent = Pair(3, "Hemo"),
            isInhibitor = false,
            category = "MEAT"
        ),
        FoodItem(
            id = 3,
            name = "Pollo",
            nutrientContent = Pair(1, "Hemo"),
            isInhibitor = false,
            category = "MEAT"
        ),
        FoodItem(
            id = 4,
            name = "Atún",
            nutrientContent = Pair(1, "Hemo"),
            isInhibitor = false,
            category = "FISH"
        ),
        FoodItem(
            id = 5,
            name = "Sardinas",
            nutrientContent = Pair(2, "Hemo"),
            isInhibitor = false,
            category = "FISH"
        ),
        FoodItem(
            id = 6,
            name = "Lentejas",
            nutrientContent = Pair(3, "No-Hemo"),
            isInhibitor = false,
            category = "LEGUME"
        ),
        FoodItem(
            id = 7,
            name = "Espinaca",
            nutrientContent = Pair(3, "No-Hemo"),
            isInhibitor = false,
            category = "VEGETABLE"
        ),
        FoodItem(
            id = 8,
            name = "Garbanzos",
            nutrientContent = Pair(3, "No-Hemo"),
            isInhibitor = false,
            category = "LEGUME"
        ),
        FoodItem(
            id = 9,
            name = "Tofu",
            nutrientContent = Pair(3, "No-Hemo"),
            isInhibitor = false,
            category = "LEGUME"
        ),
        FoodItem(
            id = 10,
            name = "Quinoa",
            nutrientContent = Pair(3, "No-Hemo"),
            isInhibitor = false,
            category = "GRAIN"
        ),
        FoodItem(
            id = 11,
            name = "Avena",
            nutrientContent = Pair(2, "No-Hemo"),
            isInhibitor = false,
            category = "GRAIN"
        ),
        FoodItem(
            id = 12,
            name = "Leche",
            nutrientContent = Pair(0, "No-Hemo"),
            isInhibitor = true,
            category = "DAIRY"
        ),
        FoodItem(
            id = 13,
            name = "Café",
            nutrientContent = Pair(0, "No-Hemo"),
            isInhibitor = true,
            category = "BEVERAGE"
        ),
        FoodItem(
            id = 14,
            name = "Jugo de naranja",
            nutrientContent = Pair(0, "No-Hemo"),
            isInhibitor = false,
            category = "BEVERAGE"
        ),
        FoodItem(
            id = 15,
            name = "Fresas",
            nutrientContent = Pair(1, "No-Hemo"),
            isInhibitor = false,
            category = "FRUIT"
        ),
    )
}

object FoodEntryDatabase {
    val foodEntries = listOf(
        // Mateo (5 entries)
        FoodEntry(
            foodItemId = 1,
            patientName = "Mateo",
            quantity = 150,
            unit = "gramos",
            ironContributed = 150 * 6 * 0.25 / 100.0,  // 2.25
            registeredAt = "2026-05-01"
        ),
        FoodEntry(
            foodItemId = 6,
            patientName = "Mateo",
            quantity = 200,
            unit = "gramos",
            ironContributed = 200 * 3 * 0.05 / 100.0,  // 0.30
            registeredAt = "2026-05-03"
        ),
        FoodEntry(
            foodItemId = 5,
            patientName = "Mateo",
            quantity = 100,
            unit = "gramos",
            ironContributed = 100 * 2 * 0.25 / 100.0,
            registeredAt = "2026-05-06"
        ),
        FoodEntry(
            foodItemId = 7,
            patientName = "Mateo",
            quantity = 120,
            unit = "gramos",
            ironContributed = 120 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-09"
        ),
        FoodEntry(
            foodItemId = 2,
            patientName = "Mateo",
            quantity = 180,
            unit = "gramos",
            ironContributed = 180 * 3 * 0.25 / 100.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 8,
            patientName = "Lucia",
            quantity = 150,
            unit = "gramos",
            ironContributed = 150 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-02"
        ),
        FoodEntry(
            foodItemId = 4,
            patientName = "Lucia",
            quantity = 100,
            unit = "mililitros",
            ironContributed = 100 * 1 * 0.25 / 100.0,
            registeredAt = "2026-05-04"
        ),
        FoodEntry(
            foodItemId = 10,
            patientName = "Lucia",
            quantity = 130,
            unit = "gramos",
            ironContributed = 130 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-07"
        ),
        FoodEntry(
            foodItemId = 12,
            patientName = "Lucia",
            quantity = 200,
            unit = "mililitros",
            ironContributed = 0.0,
            registeredAt = "2026-05-08"
        ),
        FoodEntry(
            foodItemId = 3,
            patientName = "Lucia",
            quantity = 160,
            unit = "gramos",
            ironContributed = 160 * 1 * 0.25 / 100.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 9,
            patientName = "Mateo",
            quantity = 100,
            unit = "gramos",
            ironContributed = 100 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 13,
            patientName = "Mateo",
            quantity = 200,
            unit = "mililitros",
            ironContributed = 0.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 11,
            patientName = "Mateo",
            quantity = 80,
            unit = "gramos",
            ironContributed = 80 * 2 * 0.05 / 100.0,
            registeredAt = "2026-05-04"
        ),
        FoodEntry(
            foodItemId = 15,
            patientName = "Mateo",
            quantity = 150,
            unit = "gramos",
            ironContributed = 150 * 1 * 0.05 / 100.0,
            registeredAt = "2026-05-07"
        ),
        FoodEntry(
            foodItemId = 4,
            patientName = "Mateo",
            quantity = 120,
            unit = "gramos",
            ironContributed = 120 * 1 * 0.25 / 100.0,
            registeredAt = "2026-05-08"
        ),

        FoodEntry(
            foodItemId = 14,
            patientName = "Lucia",
            quantity = 250,
            unit = "mililitros",
            ironContributed = 0.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 7,
            patientName = "Lucia",
            quantity = 100,
            unit = "gramos",
            ironContributed = 100 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-11"
        ),
        FoodEntry(
            foodItemId = 9,
            patientName = "Lucia",
            quantity = 150,
            unit = "gramos",
            ironContributed = 150 * 3 * 0.05 / 100.0,
            registeredAt = "2026-05-05"
        ),
        FoodEntry(
            foodItemId = 13,
            patientName = "Lucia",
            quantity = 150,
            unit = "mililitros",
            ironContributed = 0.0,
            registeredAt = "2026-05-06"
        ),
        FoodEntry(
            foodItemId = 15,
            patientName = "Lucia",
            quantity = 200,
            unit = "gramos",
            ironContributed = 200 * 1 * 0.05 / 100.0,
            registeredAt = "2026-05-09"
        ),
    )
}