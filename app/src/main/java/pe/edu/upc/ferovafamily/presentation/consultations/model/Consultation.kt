package pe.edu.upc.ferovafamily.presentation.consultations.model

data class Consultation(
    val id: String,
    val childId: String,
    val childName: String,
    val nurse: Nurse,
    val isOpen: Boolean = true,
    val messages: List<Message> = emptyList()
) {
    val lastMessage: String
        get() = messages.lastOrNull()?.text ?: "Sin mensajes"
}