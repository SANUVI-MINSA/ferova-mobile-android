package pe.edu.upc.ferovafamily.presentation.appointments

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upc.ferovafamily.presentation.appointments.model.Appointment
import pe.edu.upc.ferovafamily.presentation.appointments.model.HealthCenter
import pe.edu.upc.ferovafamily.presentation.appointments.model.TimeSlot
import java.time.LocalDate
import java.util.UUID

data class AppointmentsUiState(
    val healthCenters: List<HealthCenter> = emptyList(),
    val appointments: List<Appointment> = emptyList()
)

class AppointmentsViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppointmentsUiState())
    val state: StateFlow<AppointmentsUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // Coordenadas mock cerca de San Juan de Lurigancho, Lima
        val centers = listOf(
            HealthCenter(
                id = "hc-1",
                name = "Posta Medica Huascar",
                address = "Av. Huascar 1250, San Juan de Lurigancho",
                phone = "+51 01 234-5678",
                location = LatLng(-12.0250, -76.9990),
                distanceKm = 0.5,
                isActive = true,
                attentionDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie"),
                services = listOf("Control Crecimiento", "Pediatria", "Nutricion", "Suplementacion")
            ),
            HealthCenter(
                id = "hc-2",
                name = "Centro de Salud Rosa",
                address = "Av. Las Flores 480, San Juan de Lurigancho",
                phone = "+51 01 345-6789",
                location = LatLng(-12.0200, -77.0050),
                distanceKm = 1.5,
                isActive = true,
                attentionDays = listOf("Lun", "Mié", "Vie"),
                services = listOf("Pediatria", "Control Crecimiento", "Nutricion")
            ),
            HealthCenter(
                id = "hc-3",
                name = "Posta Materno Infantil",
                address = "Jr. Los Pinos 320, San Juan de Lurigancho",
                phone = "+51 01 456-7890",
                location = LatLng(-12.0280, -77.0010),
                distanceKm = 1.2,
                isActive = true,
                attentionDays = listOf("Lun", "Mar", "Jue", "Vie"),
                services = listOf("Pediatria", "Suplementacion")
            ),
            HealthCenter(
                id = "hc-4",
                name = "Centro de Salud Canto Grande",
                address = "Av. Canto Grande 1500, San Juan de Lurigancho",
                phone = "+51 01 567-8901",
                location = LatLng(-12.0220, -76.9950),
                distanceKm = 2.1,
                isActive = true,
                attentionDays = listOf("Mar", "Mié", "Jue"),
                services = listOf("Control Crecimiento", "Pediatria")
            )
        )

        _state.update { it.copy(healthCenters = centers) }
    }

    fun getCenterById(id: String): HealthCenter? =
        _state.value.healthCenters.firstOrNull { it.id == id }

    fun getAppointmentById(id: String): Appointment? =
        _state.value.appointments.firstOrNull { it.id == id }

    /**
     * Genera horarios mock para la fecha — algunos ocupados, otros libres.
     * En producción esto vendría del backend según la disponibilidad real.
     */
    fun getTimeSlotsFor(centerId: String, date: LocalDate): List<TimeSlot> {
        return listOf(
            TimeSlot("08:00", true),
            TimeSlot("09:00", true),
            TimeSlot("10:00", true),
            TimeSlot("11:00", false),
            TimeSlot("14:00", false),
            TimeSlot("15:00", false),
            TimeSlot("16:00", true)
        )
    }

    /**
     * Crea la cita y la marca como confirmada (demo simple).
     * Devuelve el ID para navegar a la pantalla de confirmación.
     */
    fun bookAppointment(
        centerId: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ): String {
        val center = getCenterById(centerId) ?: return ""
        val newId = UUID.randomUUID().toString()
        val appointment = Appointment(
            id = newId,
            healthCenterId = centerId,
            healthCenterName = center.name,
            patientId = patientId,
            patientName = patientName,
            date = date,
            time = time,
            isConfirmed = true
        )
        _state.update { it.copy(appointments = it.appointments + appointment) }
        return newId
    }
}