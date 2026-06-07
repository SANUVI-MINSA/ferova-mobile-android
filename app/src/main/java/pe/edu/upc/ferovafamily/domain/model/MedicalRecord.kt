package pe.edu.upc.ferovafamily.domain.model

data class MedicalRecord(
    val id: String,
    val patientId: String,
    val initialHemoglobin: Double,
    val diagnosis: String,
    val notes: String? = null
)
