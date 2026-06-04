package pe.edu.upc.ferovafamily.domain.model

data class Treatment(
    val id: String,
    val patientId: String,
    val startDate: String,
    val durationDays: Int,
    val ironDoseMg: Double,
    val status: String   // "ACTIVE" | "COMPLETED" | "ABANDONED"
)
