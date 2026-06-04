package pe.edu.upc.ferovafamily.domain.model

data class HemoglobinRecord(
    val date: String,
    val value: Float,
    val unit: String = "g/dL"
)
