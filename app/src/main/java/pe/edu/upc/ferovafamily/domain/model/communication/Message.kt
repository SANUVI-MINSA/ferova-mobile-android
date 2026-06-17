package pe.edu.upc.ferovafamily.domain.model.communication

data class Message(
    val id: String,
    val text: String,
    val isFromNurse: Boolean,
    val time: String
)
