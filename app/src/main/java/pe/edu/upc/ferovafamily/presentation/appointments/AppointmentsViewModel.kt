package pe.edu.upc.ferovafamily.presentation.appointments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.HealthFacilitiesApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.BookAppointmentRequest
import pe.edu.upc.ferovafamily.data.remote.dto.CancelAppointmentRequest
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.appointments.Appointment
import pe.edu.upc.ferovafamily.domain.model.appointments.HealthCenter
import pe.edu.upc.ferovafamily.domain.model.appointments.TimeSlot
import java.time.LocalDate

data class AppointmentsUiState(
    val userLocation: Pair<Double, Double> = Pair(
        -12.0250,
        -76.9990
    ),//Lat y Lng, incluidos en valor default
    val appointmentHistory: List<Appointment> = emptyList(),
    val nextAppointment: Appointment? = null,
    val cancelMessage: String? = null,
    val healthCenters: List<HealthCenter> = emptyList(),
    val selectedCenter: HealthCenter? = null,
    val appointment: Appointment? = null,
    val availableSlots: List<TimeSlot> = emptyList(),
    val patients: List<Patient> = emptyList(),
    val isLoadingNextAppointment: Boolean = false,
    val isLoadingAppointmentHistory: Boolean = false,
    val isCancelingAppointment: Boolean = false,
    val showCancelDialog: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val permissionRequested: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLoadingCenter: Boolean = false,
    val isLoadingCenters: Boolean = false,
    val isLoadingPatients: Boolean = false,
    val isLoadingSlots: Boolean = false,
    val isBooking: Boolean = false,
    val error: String? = null,
    val bookingSuccess: String? = null,   // appointmentId confirmado
)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val healthFacilitiesService =
        FerovaApiClient.create(HealthFacilitiesApiService::class.java, application)

    private val patientService =
        FerovaApiClient.create(PatientApiService::class.java, application)

    private val _state = MutableStateFlow(AppointmentsUiState())
    val state: StateFlow<AppointmentsUiState> = _state.asStateFlow()

    // Verificacion de permiso de ubicacion
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Actualizacion de estado
    fun onPermissionResult(isGranted: Boolean) {
        _state.update {
            it.copy(
                hasLocationPermission = isGranted,
                permissionRequested = true,
                isLoadingLocation = isGranted
            )
        }
        if (isGranted) getLocation()
        else {
            loadNearbyFacilities()
        }
    }

    //Obtiene la ubicacion del usuario
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (!hasLocationPermission()) return

        _state.update { it.copy(isLoadingLocation = true) }
        val fusedClient = LocationServices.getFusedLocationProviderClient(getApplication())

        // 1. Intentar obtener la última ubicación conocida (es casi instantáneo)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _state.update { state ->
                    state.copy(
                        userLocation = Pair(location.latitude, location.longitude),
                        isLoadingLocation = false
                    )
                }
                loadNearbyFacilities()
            } else {
                // 2. Si no hay ubicación previa, solicitar la ubicación actual
                requestCurrentLocation(fusedClient)
            }
        }.addOnFailureListener {
            requestCurrentLocation(fusedClient)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(fusedClient: FusedLocationProviderClient) {
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            _state.update { state ->
                state.copy(
                    userLocation = location?.let { Pair(it.latitude, it.longitude) }
                        ?: state.userLocation,
                    isLoadingLocation = false
                )
            }
            loadNearbyFacilities()
        }.addOnFailureListener { e ->
            _state.update { it.copy(error = e.message, isLoadingLocation = false) }
            loadNearbyFacilities()
        }
    }

    // ── Postas cercanas a 10 km de la ubicacion del usuario
    fun loadNearbyFacilities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCenters = true, error = null) }
            val lat = _state.value.userLocation.first
            val lng = _state.value.userLocation.second
            try {
                val response = healthFacilitiesService.getNearbyFacilities(lat, lng)
                if (response.isSuccessful) {
                    val centers = response.body()?.filter { dto ->
                        (dto.distanceKm ?: Double.MAX_VALUE) <= 10.0
                    }?.sortedBy { it.distanceKm }?.map { dto ->
                        HealthCenter(
                            id = dto.id,
                            name = dto.name,
                            address = dto.address ?: "",
                            phone = dto.phoneNumber ?: "",
                            location = LatLng(
                                dto.latitude ?: 0.0,
                                dto.longitude ?: 0.0
                            ),
                            distanceKm = dto.distanceKm ?: 0.0,
                            isActive = dto.status == "ACTIVE",
                            attentionDays = dto.availableDays ?: emptyList(),
                            services = dto.services ?: emptyList()
                        )
                    } ?: emptyList()
                    _state.update { it.copy(healthCenters = centers, isLoadingCenters = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCenters = false,
                        error = "Failed to load nearby facilities: $e"
                    )
                }
            }
        }
    }

    // Obtener HealthCenter por Id
    fun getCenterById(id: String) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isLoadingCenter = true,
                        error = null
                    )
                }
                val response = healthFacilitiesService.getFacilityDetail(id)
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        val center = HealthCenter(
                            id = id,
                            name = dto.name,
                            address = dto.address ?: "",
                            phone = dto.phoneNumber ?: "",
                            location = LatLng(
                                dto.latitude ?: 0.0,
                                dto.longitude ?: 0.0
                            ),
                            distanceKm = dto.distanceKm ?: 0.0,
                            isActive = dto.status == "ACTIVE",
                            attentionDays = dto.availableDays ?: emptyList(),
                            services = dto.services ?: emptyList()
                        )
                        _state.update {
                            it.copy(
                                selectedCenter = center,
                                isLoadingCenter = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCenter = false,
                        error = "Failed loading center: $e"
                    )
                }
            }
        }
    }

    // Obtener pacientes
    fun getPatients() {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isLoadingPatients = true,
                        error = null
                    )
                }
                val response = patientService.getMyPatients()
                if (response.isSuccessful) {
                    val dto = response.body()
                    val patientsDto = dto?.patients ?: emptyList()
                    val patients = patientsDto.map { patientDto ->
                        Patient(
                            id = patientDto.id,
                            name = patientDto.name,
                            lastName = "",
                            birthDate = "",
                            gender = "",
                            weight = 0.0,
                            height = 0.0,
                            motherId = ""
                        )
                    }
                    _state.update {
                        it.copy(
                            patients = patients,
                            isLoadingPatients = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoadingPatients = false,
                            error = "Error al cargar pacientes"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingPatients = false,
                        error = "Failed loading patients: $e"
                    )
                }
            }
        }
    }

    // ── Slots disponibles ─────────────────────────────────────────────────────
    fun loadAvailableSlots(centerId: String, date: LocalDate) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingSlots = true,
                    availableSlots = emptyList(),
                    bookingSuccess = null,
                    appointment = null
                )
            }
            try {
                val dateStr = date.toString()
                val response = healthFacilitiesService.getAvailableSlots(centerId, dateStr)
                if (response.isSuccessful) {
                    response.body()?.let { slots ->
                        val slots = slots.map { dto ->
                            TimeSlot(
                                time = dto.time,
                                isAvailable = dto.status == "AVAILABLE"
                            )
                        }
                        _state.update { it.copy(availableSlots = slots, isLoadingSlots = false) }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoadingSlots = false,
                            error = response.errorBody()?.string()
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingSlots = false,
                        error = "Failed to load available slots: $e"
                    )
                }
            }
        }
    }

    //Traduccion del texto de error
    private fun translateError(errorMessage: String): String {
        return when {
            errorMessage.contains("This schedule is already reserved") ->
                "El horario elegido ya está ocupado"

            errorMessage.contains("This facility has no assigned nurse") ->
                "La posta no tiene un enfermero designado"

            errorMessage.contains("Appointment not found") -> "La cita no existe"

            else -> "Error desconocido"
        }
    }

    // ── Reservar cita ─────────────────────────────────────────────────────────
    fun bookAppointment(
        centerId: String,
        centerName: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isBooking = true, error = null, bookingSuccess = null) }
            try {
                val request = BookAppointmentRequest(
                    facilityId = centerId,
                    patientId = patientId,
                    appointmentDate = date.toString(),
                    appointmentTime = time
                )
                val response = healthFacilitiesService.bookAppointment(request)
                if (response.isSuccessful) {
                    val appointment = Appointment(
                        id = "",
                        healthCenterId = centerId,
                        healthCenterName = centerName,
                        patientId = patientId,
                        patientName = patientName,
                        date = date,
                        time = time,
                    )
                    _state.update {
                        it.copy(
                            appointment = appointment,
                            isBooking = false,
                            bookingSuccess = "OK"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val rawError = try {
                        JSONObject(errorBody ?: "").getString("error")
                    } catch (_: Exception) {
                        ""
                    }
                    _state.update {
                        it.copy(
                            isBooking = false,
                            error = translateError(rawError)
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isBooking = false,
                        error = "Failed to create appointment: $e"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    // Obtener siguiente cita medica
    fun loadNextAppointment() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingNextAppointment = true,
                    error = null
                )
            }
            try {
                val response = healthFacilitiesService.getMotherNextAppointment()
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        if (dto.message != null) {
                            // No hay cita próxima
                            _state.update {
                                it.copy(
                                    nextAppointment = null,
                                    isLoadingNextAppointment = false
                                )
                            }
                        } else {
                            // Hay cita próxima
                            _state.update {
                                it.copy(
                                    nextAppointment = Appointment(
                                        id = dto.id,
                                        healthCenterId = "",
                                        healthCenterName = dto.facilityName ?: "",
                                        patientId = dto.patientId ?: "",
                                        patientName = "",
                                        date = LocalDate.parse(
                                            dto.appointmentDate ?: LocalDate.now().toString()
                                        ),
                                        time = dto.appointmentTime ?: "",
                                        isConfirmed = dto.status == "CONFIRMED"
                                    ),
                                    isLoadingNextAppointment = false
                                )
                            }
                        }
                    } ?: _state.update { it.copy(isLoadingNextAppointment = false) }
                } else {
                    _state.update { it.copy(isLoadingNextAppointment = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingNextAppointment = false,
                        error = "Failed to load next appointment: $e"
                    )
                }
            }
        }
    }

    //Obtener historial de citas
    fun loadAppointmentHistory(patientId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingAppointmentHistory = true,
                    error = null,
                )
            }
            try {
                val response = healthFacilitiesService.getPatientAppointments(patientId)
                if (response.isSuccessful) {
                    response.body()?.let { appointments ->
                        val history = appointments.map { dto ->
                            Appointment(
                                id = dto.id,
                                healthCenterId = "",
                                healthCenterName = dto.facilityName ?: "",
                                patientId = dto.patientId ?: "",
                                patientName = "",
                                date = LocalDate.parse(
                                    dto.appointmentDate ?: LocalDate.now().toString()
                                ),
                                time = dto.appointmentTime ?: "",
                                isConfirmed = dto.status == "CONFIRMED"
                            )
                        }
                        _state.update {
                            it.copy(
                                appointmentHistory = history,
                                isLoadingAppointmentHistory = false
                            )
                        }
                    } ?: _state.update { it.copy(isLoadingAppointmentHistory = false) }
                } else {
                    _state.update { it.copy(isLoadingAppointmentHistory = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingAppointmentHistory = false,
                        error = "Failed to load appointment history: $e"
                    )
                }
            }
        }
    }

    //Cancelar cita
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCancelingAppointment = true,
                    error = null
                )
            }
            try {
                val request = CancelAppointmentRequest(appointmentId)
                val response = healthFacilitiesService.cancelAppointment(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val message = it.message!!
                        _state.update { state ->
                            state.copy(
                                isCancelingAppointment = false,
                                showCancelDialog = false,
                                cancelMessage = message
                            )
                        }
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val rawError = try {
                        JSONObject(errorBody ?: "").getString("error")
                    } catch (_: Exception) {
                        ""
                    }
                    _state.update {
                        it.copy(
                            isBooking = false,
                            error = translateError(rawError)
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isCancelingAppointment = false,
                        error = "Failed to cancel appointment: $e"
                    )
                }
            }
        }
    }

    // ── Limpiar mensaje de cancelacion al cerrar popup ────────────────────────────────────
    fun clearCancelMessage() {
        _state.update { it.copy(cancelMessage = null) }
    }

    //Limpiar el estado de reserva (éxito y cita creada)
    fun resetBookingState() {
        _state.update { it.copy(bookingSuccess = null, appointment = null) }
    }

    //Eliminar la cita siguiente
    fun clearNextAppointment() {
        _state.update { it.copy(nextAppointment = null) }
    }
}
