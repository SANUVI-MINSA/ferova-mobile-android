package pe.edu.upc.ferovafamily.domain.model.appointments

import java.time.LocalDate

data class Appointment(
    val id: String,
    val healthCenterId: String,
    val healthCenterName: String,
    val patientId: String,
    val patientName: String,
    val date: LocalDate,
    val time: String,
    val isConfirmed: Boolean = true
)