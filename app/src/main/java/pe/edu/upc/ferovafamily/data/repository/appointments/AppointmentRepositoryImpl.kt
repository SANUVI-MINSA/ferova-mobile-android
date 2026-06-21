package pe.edu.upc.ferovafamily.data.repository.appointments

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import pe.edu.upc.ferovafamily.data.remote.api.HealthFacilitiesApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.BookAppointmentRequest
import pe.edu.upc.ferovafamily.data.remote.dto.CancelAppointmentRequest
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.appointments.Appointment
import pe.edu.upc.ferovafamily.domain.model.appointments.HealthCenter
import pe.edu.upc.ferovafamily.domain.model.appointments.TimeSlot
import pe.edu.upc.ferovafamily.domain.repository.appointments.AppointmentRepository
import java.time.LocalDate

class AppointmentRepositoryImpl(
    private val healthFacilitiesService: HealthFacilitiesApiService,
    private val patientService: PatientApiService
) : AppointmentRepository {

    override suspend fun loadNearbyFacilities(
        lat: Double,
        lng: Double
    ): List<HealthCenter> {
        android.util.Log.d("POSTAS", "📍 loadNearbyFacilities called: lat=$lat, lng=$lng")

        val response = healthFacilitiesService.getNearbyFacilities(lat, lng)
        android.util.Log.d("POSTAS", "📍 Response code: ${response.code()}")

        if (!response.isSuccessful) {
            android.util.Log.e("POSTAS", "📍 Error: ${response.errorBody()?.string()}")
            return emptyList()
        }

        val body = response.body()
        android.util.Log.d("POSTAS", "📍 Body size: ${body?.size ?: 0}")

        body?.forEachIndexed { index, dto ->
            android.util.Log.d("POSTAS", "📍 [$index] ${dto.name} - ${dto.distanceKm} km")
        }

        return body?.filter { dto ->
            (dto.distanceKm ?: Double.MAX_VALUE) <= 100.0
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
    }

    override suspend fun getCenterById(id: String): HealthCenter? {
        val response = healthFacilitiesService.getFacilityDetail(id)
        if (!response.isSuccessful) return null

        return response.body()?.let { dto ->
            HealthCenter(
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
        }
    }

    override suspend fun getPatients(): List<Patient> {
        android.util.Log.d("APPOINTMENTS_REPO", "🔄 getPatients - Llamando a API...")
        val response = patientService.getMyPatients()
        android.util.Log.d("APPOINTMENTS_REPO", "📥 Response code: ${response.code()}")

        if (!response.isSuccessful) {
            android.util.Log.e("APPOINTMENTS_REPO", "❌ Error: ${response.errorBody()?.string()}")
            return emptyList()
        }

        val patients = response.body()?.patients?.map { dto ->
            Patient(
                id = dto.id,
                name = dto.name,
                lastName = "",
                birthDate = "",
                gender = "",
                weight = 0.0,
                height = 0.0,
                motherId = ""
            )
        } ?: emptyList()

        android.util.Log.d("APPOINTMENTS_REPO", "✅ Pacientes recibidos: ${patients.size}")
        patients.forEach { patient ->
            android.util.Log.d("APPOINTMENTS_REPO", "   - ${patient.name} (ID: ${patient.id})")
        }

        return patients
    }

    override suspend fun loadAvailableSlots(
        centerId: String,
        date: LocalDate
    ): List<TimeSlot> {
        val response = healthFacilitiesService.getAvailableSlots(centerId, date.toString())
        if (!response.isSuccessful) return emptyList()

        return response.body()?.map { dto ->
            TimeSlot(
                time = dto.time,
                isAvailable = dto.status == "AVAILABLE"
            )
        } ?: emptyList()
    }

    override suspend fun bookAppointment(
        centerId: String,
        centerName: String,
        patientId: String,
        patientName: String,
        date: LocalDate,
        time: String
    ): String {
        val request = BookAppointmentRequest(
            facilityId = centerId,
            patientId = patientId,
            appointmentDate = date.toString(),
            appointmentTime = time
        )
        val response = healthFacilitiesService.bookAppointment(request)

        if (response.isSuccessful) return "OK"

        val errorBody = response.errorBody()?.string()
        val rawError = try {
            JSONObject(errorBody ?: "").getString("error")
        } catch (_: Exception) {
            ""
        }

        throw Exception(rawError)
    }

    override suspend fun getNextAppointment(): Appointment? {
        val response = healthFacilitiesService.getMotherNextAppointment()
        if (!response.isSuccessful) return null

        return response.body()?.let { dto ->
            if (dto.message != null) return null

            Appointment(
                id = dto.id,
                healthCenterId = "",
                healthCenterName = dto.facilityName ?: "",
                patientId = dto.patientId ?: "",
                patientName = "",
                date = LocalDate.parse(dto.appointmentDate ?: LocalDate.now().toString()),
                time = dto.appointmentTime ?: "",
                isConfirmed = dto.status == "CONFIRMED"
            )
        }
    }

    override suspend fun getAppointmentHistory(patientId: String): List<Appointment> {
        val response = healthFacilitiesService.getPatientAppointments(patientId)
        if (!response.isSuccessful) return emptyList()

        return response.body()?.map { dto ->
            Appointment(
                id = dto.id,
                healthCenterId = "",
                healthCenterName = dto.facilityName ?: "",
                patientId = dto.patientId ?: "",
                patientName = "",
                date = LocalDate.parse(dto.appointmentDate ?: LocalDate.now().toString()),
                time = dto.appointmentTime ?: "",
                isConfirmed = dto.status == "CONFIRMED"
            )
        } ?: emptyList()
    }

    override suspend fun cancelAppointment(appointmentId: String): String? {
        val request = CancelAppointmentRequest(appointmentId)
        val response = healthFacilitiesService.cancelAppointment(request)

        if (response.isSuccessful) return response.body()?.message

        val errorBody = response.errorBody()?.string()
        val rawError = try {
            JSONObject(errorBody ?: "").getString("error")
        } catch (_: Exception) {
            ""
        }

        throw Exception(rawError)
    }
}