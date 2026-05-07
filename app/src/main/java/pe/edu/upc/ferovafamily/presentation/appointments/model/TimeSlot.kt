package pe.edu.upc.ferovafamily.presentation.appointments.model

data class TimeSlot(
    val time: String,        // "08:00", "09:00"
    val isAvailable: Boolean
)