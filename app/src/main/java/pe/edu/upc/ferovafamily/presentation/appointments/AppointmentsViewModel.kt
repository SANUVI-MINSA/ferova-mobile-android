package pe.edu.upc.ferovafamily.presentation.appointments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.HealthFacilitiesApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.BookAppointmentRequest
import pe.edu.upc.ferovafamily.presentation.appointments.model.Appointment
import pe.edu.upc.ferovafamily.presentation.appointments.model.HealthCenter
import pe.edu.upc.ferovafamily.presentation.appointments.model.TimeSlot
import java.time.LocalDate
import java.util.UUID

data class AppointmentsUiState(
    val userLocation: Pair<Double, Double> = Pair(
        -12.0250,
        -76.9990
    ),//Lat y Lng, incluidos en valor default
    val healthCenters: List<HealthCenter> = emptyList(),
    val selectedCenter: HealthCenter? = null,
    val appointment: Appointment? = null,
    val availableSlots: List<TimeSlot> = emptyList(),
    val patients: List<Map<String, String>> = emptyList(),
    val hasLocationPermission: Boolean = false,
    val permissionRequested: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLoadingCenter: Boolean = false,
    val isLoadingCenters: Boolean = false,
    val isLoadingPatients: Boolean = false,
    val isLoadingSlots: Boolean = false,
    val isBooking: Boolean = false,
    val error: String? = null,
    val bookingSuccess: String? = null   // appointmentId confirmado
)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
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
    private fun requestCurrentLocation(fusedClient: com.google.android.gms.location.FusedLocationProviderClient) {
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
                val response = patientService.getMotherPatients()
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        _state.update {
                            it.copy(
                                patients = dto.patients,
                                isLoadingPatients = false
                            )
                        }
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
            _state.update { it.copy(isLoadingSlots = true, availableSlots = emptyList()) }
            try {
                val dateStr = date.toString()   // "2026-06-10"
                val response = healthFacilitiesService.getAvailableSlots(centerId, dateStr)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val slots = it.map { dto ->
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

    // ── Reservar cita ─────────────────────────────────────────────────────────
    fun bookAppointment(
        centerId: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isBooking = true, error = null) }
            try {
                val request = BookAppointmentRequest(
                    facilityId = centerId,
                    patientId = patientId,
                    appointmentDate = date.toString(),
                    appointmentTime = time
                )
                val response = healthFacilitiesService.bookAppointment(request)
                if (response.isSuccessful) {
                    val dto = response.body()
                    val confirmedId = response.body()?.id ?: ""
                    val appointment = Appointment(
                        id = confirmedId,
                        healthCenterId = centerId,
                        healthCenterName = "",
                        patientId = patientId,
                        patientName = patientName,
                        date = date,
                        time = time,
                        isConfirmed = true
                    )
                    _state.update {
                        it.copy(
                            appointment = appointment,
                            isBooking = false,
                            bookingSuccess = confirmedId
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isBooking = false,
                            error = "No se pudo reservar la cita: ${
                                response.errorBody()?.toString()
                            }"
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
}
