package pe.edu.upc.ferovafamily.presentation.consultations.model

data class Nurse(
    val id: String,
    val name: String,
    val specialty: String = "Enfermera asignada"
)