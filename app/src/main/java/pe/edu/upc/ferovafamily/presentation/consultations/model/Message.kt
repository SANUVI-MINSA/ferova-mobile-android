package pe.edu.upc.ferovafamily.presentation.consultations.model

data class Message(
    val id: String,
    val text: String,
    val isFromNurse: Boolean,
    val time: String
)