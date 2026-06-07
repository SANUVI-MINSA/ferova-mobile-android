package pe.edu.upc.ferovafamily.domain.model

data class Patient(
    val id: String,
    val name: String,
    val lastName: String,
    val birthDate: String,
    val gender: String,
    val weight: Double,
    val height: Double,
    val motherId: String? = null
) {
    val fullName: String get() = "$name $lastName".trim()
}
