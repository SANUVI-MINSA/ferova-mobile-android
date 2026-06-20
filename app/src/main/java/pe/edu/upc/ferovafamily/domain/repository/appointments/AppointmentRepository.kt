package pe.edu.upc.ferovafamily.domain.repository.appointments

import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.appointments.Appointment
import pe.edu.upc.ferovafamily.domain.model.appointments.HealthCenter
import pe.edu.upc.ferovafamily.domain.model.appointments.TimeSlot
import java.time.LocalDate

interface AppointmentRepository {
    suspend fun loadNearbyFacilities(lat: Double, lng: Double): List<HealthCenter>
    suspend fun getCenterById(id: String): HealthCenter?
    suspend fun getPatients(): List<Patient>
    suspend fun loadAvailableSlots(centerId: String, date: LocalDate): List<TimeSlot>
    suspend fun bookAppointment(
        centerId: String,
        centerName: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ): String?

    suspend fun getNextAppointment(): Appointment?
    suspend fun getAppointmentHistory(patientId: String): List<Appointment>
    suspend fun cancelAppointment(appointmentId: String): String?
}