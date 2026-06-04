package pe.edu.upc.ferovafamily.domain.model

data class TodayDose(
    val patientId: String,
    val canConfirm: Boolean,
    val scheduledTime: String,
    val confirmedAt: String? = null
)
