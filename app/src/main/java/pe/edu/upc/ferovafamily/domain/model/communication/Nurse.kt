package pe.edu.upc.ferovafamily.domain.model.communication

data class Nurse(
    val id: String,
    val name: String,
    val specialty: String = "Enfermera asignada",
    val email: String = "",
    val isOnline: Boolean = false
)
