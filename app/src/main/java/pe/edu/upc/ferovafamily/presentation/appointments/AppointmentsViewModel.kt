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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.HealthFacilitiesApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.repository.appointments.AppointmentRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.appointments.Appointment
import pe.edu.upc.ferovafamily.domain.model.appointments.HealthCenter
import pe.edu.upc.ferovafamily.domain.model.appointments.TimeSlot
import pe.edu.upc.ferovafamily.domain.repository.appointments.AppointmentRepository
import java.time.LocalDate

data class AppointmentsUiState(
    val userLocation: Pair<Double, Double> = Pair(
        -12.0250,
        -76.9990
    ),
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
    val bookingSuccess: String? = null,
)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppointmentRepository = AppointmentRepositoryImpl(
        FerovaApiClient.create(HealthFacilitiesApiService::class.java, application),
        FerovaApiClient.create(PatientApiService::class.java, application)
    )

    private val _state = MutableStateFlow(AppointmentsUiState())
    val state: StateFlow<AppointmentsUiState> = _state.asStateFlow()

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        android.util.Log.d("POSTAS", "📍 hasLocationPermission: ${hasLocationPermission()}")

        if (!hasLocationPermission()) {
            android.util.Log.d("POSTAS", "📍 No tiene permiso de ubicación")
            return
        }

        _state.update { it.copy(isLoadingLocation = true) }
        val fusedClient = LocationServices.getFusedLocationProviderClient(getApplication())

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

    fun loadNearbyFacilities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCenters = true, error = null) }
            try {
                val lat = _state.value.userLocation.first
                val lng = _state.value.userLocation.second
                val centers = repository.loadNearbyFacilities(lat, lng)
                _state.update { it.copy(healthCenters = centers, isLoadingCenters = false) }
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

    fun getCenterById(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCenter = true, error = null) }
            try {
                val center = repository.getCenterById(id)
                _state.update { it.copy(selectedCenter = center, isLoadingCenter = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoadingCenter = false, error = "Failed loading center: $e")
                }
            }
        }
    }

    fun getPatients() {
        android.util.Log.d("APPOINTMENTS_VM", "🔄 getPatients - Cargando pacientes...")
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPatients = true, error = null) }
            try {
                val patients = repository.getPatients()
                android.util.Log.d("APPOINTMENTS_VM", "✅ Pacientes cargados: ${patients.size}")
                patients.forEach { patient ->
                    android.util.Log.d("APPOINTMENTS_VM", "   - ${patient.name} (ID: ${patient.id})")
                }
                _state.update { it.copy(patients = patients, isLoadingPatients = false) }
            } catch (e: Exception) {
                android.util.Log.e("APPOINTMENTS_VM", "❌ Error cargando pacientes: ${e.message}")
                _state.update {
                    it.copy(isLoadingPatients = false, error = "Failed loading patients: $e")
                }
            }
        }
    }

    /**
     * ✅ REFRESCAR PACIENTES - Útil después de agregar un nuevo paciente
     */
    fun refreshPatients() {
        android.util.Log.d("APPOINTMENTS_VM", "🔄 refreshPatients - REFRESCANDO PACIENTES...")
        android.util.Log.d("APPOINTMENTS_VM", "   Pacientes antes: ${_state.value.patients.size}")
        getPatients()
    }

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
                val slots = repository.loadAvailableSlots(centerId, date)
                _state.update { it.copy(availableSlots = slots, isLoadingSlots = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoadingSlots = false, error = "Failed to load available slots: $e")
                }
            }
        }
    }

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
                repository.bookAppointment(centerId, centerName, patientId, patientName, date, time)
                _state.update {
                    it.copy(
                        appointment = Appointment(
                            id = "",
                            healthCenterId = centerId,
                            healthCenterName = centerName,
                            patientId = patientId,
                            patientName = patientName,
                            date = date,
                            time = time
                        ),
                        isBooking = false,
                        bookingSuccess = "OK"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isBooking = false,
                        error = translateError(e.message ?: "")
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun loadNextAppointment() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingNextAppointment = true, error = null) }
            try {
                val nextAppointment = repository.getNextAppointment()
                _state.update {
                    it.copy(
                        nextAppointment = nextAppointment,
                        isLoadingNextAppointment = false
                    )
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

    fun loadAppointmentHistory(patientId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAppointmentHistory = true, error = null) }
            try {
                val history = repository.getAppointmentHistory(patientId)
                _state.update {
                    it.copy(
                        appointmentHistory = history,
                        isLoadingAppointmentHistory = false
                    )
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

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCancelingAppointment = true, error = null) }
            try {
                val message = repository.cancelAppointment(appointmentId)
                _state.update {
                    it.copy(
                        isCancelingAppointment = false,
                        showCancelDialog = false,
                        cancelMessage = message
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isCancelingAppointment = false,
                        error = translateError(e.message ?: "")
                    )
                }
            }
        }
    }

    fun clearCancelMessage() {
        _state.update { it.copy(cancelMessage = null) }
    }

    fun resetBookingState() {
        _state.update { it.copy(bookingSuccess = null, appointment = null) }
    }

    fun clearNextAppointment() {
        _state.update { it.copy(nextAppointment = null) }
    }
}