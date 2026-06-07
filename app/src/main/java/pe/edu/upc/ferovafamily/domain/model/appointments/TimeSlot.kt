package pe.edu.upc.ferovafamily.domain.model.appointments

data class TimeSlot(
    val time: String,        // "08:00", "09:00"
    val isAvailable: Boolean
)