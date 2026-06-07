package pe.edu.upc.ferovafamily.domain.model.appointments

import com.google.android.gms.maps.model.LatLng

data class HealthCenter(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val location: LatLng,
    val distanceKm: Double,
    val isActive: Boolean = true,
    val attentionDays: List<String> = emptyList(),
    val services: List<String> = emptyList()
)