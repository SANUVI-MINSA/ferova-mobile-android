package pe.edu.upc.ferovafamily.domain.model

data class DoseRecord(
    val id: String,
    val patientId: String,
    val date: String,
    val confirmedAt: String? = null,
    val status: String   // "CONFIRMED" | "OMITTED"
)
