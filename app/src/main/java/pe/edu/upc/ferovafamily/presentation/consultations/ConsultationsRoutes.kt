package pe.edu.upc.ferovafamily.presentation.consultations

object ConsultationsRoutes {
    const val CONSULTATIONS = "consultations"
    const val MY_CONSULTATIONS = "my_consultations"
    const val NEW_CONSULTATION = "new_consultation/{childId}"
    const val CHAT = "chat/{consultationId}"

    fun newConsultation(childId: String) = "new_consultation/$childId"
    fun chat(consultationId: String) = "chat/$consultationId"
}