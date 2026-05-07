package pe.edu.upc.ferovafamily.presentation.consultations.model

data class Child(
    val id: String,
    val name: String,
    val age: Int,
    val description: String = ""
)