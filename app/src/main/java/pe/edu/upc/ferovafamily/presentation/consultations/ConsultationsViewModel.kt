package pe.edu.upc.ferovafamily.presentation.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.presentation.consultations.model.Child
import pe.edu.upc.ferovafamily.presentation.consultations.model.Consultation
import pe.edu.upc.ferovafamily.presentation.consultations.model.Message
import pe.edu.upc.ferovafamily.presentation.consultations.model.Nurse
import java.time.LocalTime
import java.util.UUID

data class ConsultationsUiState(
    val hasNurse: Boolean = true,
    val nurse: Nurse? = null,
    val children: List<Child> = emptyList(),
    val consultations: List<Consultation> = emptyList(),
    val isLoading: Boolean = false
)

class ConsultationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultationsUiState())
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val nurse = Nurse(
            id = "nurse-1",
            name = "Enf. Elena García",
            specialty = "Enfermera asignada"
        )

        val children = listOf(
            Child(
                id = "child-1",
                name = "Mateo",
                age = 5,
                description = "En tratamiento de anemia"
            ),
            Child(
                id = "child-2",
                name = "Lucía",
                age = 3,
                description = "Refuerzo nutricional"
            )
        )

        val mateoConsultation = Consultation(
            id = "consultation-1",
            childId = "child-1",
            childName = "Mateo",
            nurse = nurse,
            isOpen = true,
            messages = listOf(
                Message(
                    id = "msg-1",
                    text = "Buenas tardes, una consulta sobre las " +
                            "horas debidas en la implementación de hierro a Mateo.",
                    isFromNurse = false,
                    time = "10:30"
                )
            )
        )

        _uiState.update {
            it.copy(
                hasNurse = true,
                nurse = nurse,
                children = children,
                consultations = listOf(mateoConsultation)
            )
        }
    }

    /** Returns the open consultation for the given child, or null if none exists. */
    fun getOpenConsultationFor(childId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.childId == childId && it.isOpen }

    fun getConsultationById(consultationId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.id == consultationId }

    /** Creates a new consultation with the first message and returns its id. */
    fun createConsultation(childId: String, firstMessage: String): String {
        val child = _uiState.value.children.first { it.id == childId }
        val nurse = _uiState.value.nurse ?: return ""
        val newId = UUID.randomUUID().toString()

        val consultation = Consultation(
            id = newId,
            childId = child.id,
            childName = child.name,
            nurse = nurse,
            isOpen = true,
            messages = listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    text = firstMessage,
                    isFromNurse = false,
                    time = currentTime()
                )
            )
        )

        _uiState.update { it.copy(consultations = it.consultations + consultation) }
        simulateNurseReply(newId)
        return newId
    }

    fun sendMessage(consultationId: String, text: String) {
        if (text.isBlank()) return

        _uiState.update { state ->
            state.copy(
                consultations = state.consultations.map { consultation ->
                    if (consultation.id == consultationId) {
                        consultation.copy(
                            messages = consultation.messages + Message(
                                id = UUID.randomUUID().toString(),
                                text = text.trim(),
                                isFromNurse = false,
                                time = currentTime()
                            )
                        )
                    } else consultation
                }
            )
        }
        simulateNurseReply(consultationId)
    }

    private fun simulateNurseReply(consultationId: String) {
        viewModelScope.launch {
            delay(1500)
            _uiState.update { state ->
                state.copy(
                    consultations = state.consultations.map { consultation ->
                        if (consultation.id == consultationId) {
                            consultation.copy(
                                messages = consultation.messages + Message(
                                    id = UUID.randomUUID().toString(),
                                    text = "Recibido. Le respondo en breve con " +
                                            "indicaciones precisas.",
                                    isFromNurse = true,
                                    time = currentTime()
                                )
                            )
                        } else consultation
                    }
                )
            }
        }
    }

    private fun currentTime(): String {
        val now = LocalTime.now()
        return "%02d:%02d".format(now.hour, now.minute)
    }
}