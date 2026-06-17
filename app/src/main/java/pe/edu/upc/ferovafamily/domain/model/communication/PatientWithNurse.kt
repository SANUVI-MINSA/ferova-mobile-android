package pe.edu.upc.ferovafamily.domain.model.communication

/**
 * Paciente (hijo) de la madre junto con el estado de asignación de su enfermera.
 * Alimenta la pantalla "Nueva Consulta — ¿Sobre quién es tu consulta?".
 */
data class PatientWithNurse(
    val patientId: String,
    val patientName: String,
    val hasNurse: Boolean,
    val nurse: Nurse?
)
