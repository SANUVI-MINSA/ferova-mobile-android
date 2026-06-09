package pe.edu.upc.ferovafamily.domain.model

data class DoseHistory(
    val patientId: String,
    val patientName: String,
    val supplementName: String,
    val quantity: String,
    val dosingHours: String,
    val doses: List<DoseRecord>
)
