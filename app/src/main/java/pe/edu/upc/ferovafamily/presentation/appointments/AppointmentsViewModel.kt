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
    val appointments: List<Appointment> = emptyList(),
    val availableSlots: List<TimeSlot> = emptyList(),
    val hasLocationPermission: Boolean = false,
    val permissionRequested: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLoadingCenter: Boolean = false,
    val isLoadingCenters: Boolean = false,
    val isLoadingSlots: Boolean = false,
    val isBooking: Boolean = false,
    val error: String? = null,
    val bookingSuccess: String? = null   // appointmentId confirmado
)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val service =
        FerovaApiClient.create(HealthFacilitiesApiService::class.java, application)

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

        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
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

    // ── Postas cercanas a 15 km de la ubicacion del usuario
    fun loadNearbyFacilities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCenters = true, error = null) }
            val lat = _state.value.userLocation.first
            val lng = _state.value.userLocation.second
            try {
                val response = service.getNearbyFacilities(lat, lng)
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
                } else {
                    _state.update {
                        it.copy(
                            isLoadingCenters = false,
                            error = "Failed to load facilities"
                        )
                    }
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
                val response = service.getFacilityDetail(id)
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        val center = HealthCenter(
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

    // ── Detalle de posta ──────────────────────────────────────────────────────
    fun loadFacilityDetail(centerId: String) {
        viewModelScope.launch {
            try {
                val response = service.getFacilityDetail(centerId)
                if (response.isSuccessful) {
                    val dto = response.body() ?: return@launch
                    val center = HealthCenter(
                        id = dto.id,
                        name = dto.name,
                        address = dto.address ?: "",
                        phone = dto.phoneNumber ?: "",
                        location = LatLng(dto.latitude ?: 0.0, dto.longitude ?: 0.0),
                        distanceKm = dto.distanceKm ?: 0.0,
                        isActive = dto.status == "ACTIVE",
                        attentionDays = dto.availableDays ?: emptyList(),
                        services = dto.services ?: emptyList()
                    )
                    // Actualizar o agregar al listado si no existe
                    val current = _state.value.healthCenters.toMutableList()
                    val idx = current.indexOfFirst { it.id == centerId }
                    if (idx >= 0) current[idx] = center else current.add(center)
                    _state.update { it.copy(healthCenters = current) }
                }
            } catch (_: Exception) { /* mantener datos existentes */
            }
        }
    }

    // ── Slots disponibles ─────────────────────────────────────────────────────
    fun loadAvailableSlots(centerId: String, date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSlots = true, availableSlots = emptyList()) }
            try {
                val dateStr = date.toString()   // "2026-06-10"
                val response = service.getAvailableSlots(centerId, dateStr)
                if (response.isSuccessful) {
                    val slots = response.body()?.slots?.map { time ->
                        TimeSlot(time = time, isAvailable = true)
                    } ?: emptyList()
                    _state.update { it.copy(availableSlots = slots, isLoadingSlots = false) }
                } else {
                    _state.update { it.copy(isLoadingSlots = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingSlots = false) }
            }
        }
    }

    /** Para compatibilidad con pantallas que llaman este método directamente */
    fun getTimeSlotsFor(centerId: String, date: LocalDate): List<TimeSlot> {
        return _state.value.availableSlots.ifEmpty {
            listOf("08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00")
                .map { TimeSlot(it, true) }
        }
    }

    // ── Reservar cita ─────────────────────────────────────────────────────────

    fun bookAppointment(
        centerId: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ): String {
        val tempId = UUID.randomUUID().toString()
        viewModelScope.launch {
            _state.update { it.copy(isBooking = true, error = null) }
            try {
                val request = BookAppointmentRequest(
                    facilityId = centerId,
                    patientId = patientId,
                    appointmentDate = date.toString(),
                    appointmentTime = time
                )
                val response = service.bookAppointment(request)
                if (response.isSuccessful) {
                    val dto = response.body()
                    val confirmedId = dto?.id ?: tempId
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
                            appointments = it.appointments + appointment,
                            isBooking = false,
                            bookingSuccess = confirmedId
                        )
                    }
                } else {
                    _state.update {
                        it.copy(isBooking = false, error = "No se pudo reservar la cita")
                    }
                    // Fallback: guardar localmente
                    saveLocalAppointment(centerId, patientId, patientName, date, time, tempId)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isBooking = false) }
                saveLocalAppointment(centerId, patientId, patientName, date, time, tempId)
            }
        }
        return tempId
    }

    private fun saveLocalAppointment(
        centerId: String, patientId: String, patientName: String,
        date: LocalDate, time: String, id: String
    ) {
        val appointment = Appointment(
            id = id,
            healthCenterId = centerId,
            healthCenterName = "",
            patientId = patientId,
            patientName = patientName,
            date = date,
            time = time,
            isConfirmed = true
        )
        _state.update { it.copy(appointments = it.appointments + appointment, bookingSuccess = id) }
    }

    fun getAppointmentById(id: String): Appointment? =
        _state.value.appointments.firstOrNull { it.id == id }

    fun clearError() = _state.update { it.copy(error = null) }
    fun clearBookingSuccess() = _state.update { it.copy(bookingSuccess = null) }
}
