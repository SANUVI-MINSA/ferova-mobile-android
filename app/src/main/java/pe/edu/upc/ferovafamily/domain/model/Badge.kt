package pe.edu.upc.ferovafamily.domain.model

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val targetProgress: Int,
    val unlockedAt: String? = null,
    val category: String = ""
)
