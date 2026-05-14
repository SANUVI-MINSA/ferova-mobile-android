package pe.edu.upc.ferovafamily.presentation.consultations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.ConsultationApiService
import pe.edu.upc.ferovafamily.data.remote.dto.SendMessageRequest
import pe.edu.upc.ferovafamily.data.remote.dto.StartConsultationRequest
import pe.edu.upc.ferovafamily.presentation.consultations.model.Child
import pe.edu.upc.ferovafamily.presentation.consultations.model.Consultation
import pe.edu.upc.ferovafamily.presentation.consultations.model.Message
import pe.edu.upc.ferovafamily.presentation.consultations.model.Nurse
import java.time.LocalTime
import java.util.UUID

data class ConsultationsUiState(
    val hasNurse: Boolean = false,
    val nurse: Nurse? = null,
    val children: List<Child> = emptyList(),
    val consultations: List<Consultation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ConsultationsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val service = FerovaApiClient.create(ConsultationApiService::class.java, application)

    private val _uiState = MutableStateFlow(ConsultationsUiState())
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    // ── Carga inicial ─────────────────────────────────────────────────────────

    private fun loadData() {
        val motherId = tokenManager.userId ?: return loadMockData()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadChildrenAndNurseInfo(motherId)
            loadMotherConsultations(motherId)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadChildrenAndNurseInfo(motherId: String) {
        try {
            val patientsResponse = service.getPatientsByMother(motherId)
            if (patientsResponse.isSuccessful) {
                val patients = patientsResponse.body() ?: emptyList()
                val children = patients.map { p ->
                    // Calcular edad desde birthDate
                    val age = p.birthDate?.let { calculateAge(it) } ?: 0
                    Child(
                        id = p.id,
                        name = "${p.name} ${p.lastName}".trim(),
                        age = age,
                        description = if (p.gender == "MALE") "Niño en tratamiento" else "Niña en tratamiento"
                    )
                }
                _uiState.update { it.copy(children = children) }

                // Verificar si el primer paciente tiene enfermera asignada
                val firstPatient = patients.firstOrNull()
                if (firstPatient != null) {
                    loadNurseInfo(firstPatient.id)
                }
            } else {
                loadMockChildren()
            }
        } catch (e: Exception) {
            loadMockChildren()
        }
    }

    private suspend fun loadNurseInfo(patientId: String) {
        try {
            val nurseResponse = service.getNurseInfo(patientId)
            if (nurseResponse.isSuccessful) {
                val dto = nurseResponse.body()
                val hasNurse = dto?.hasNurse ?: false
                val nurse = if (hasNurse && dto?.nurseId != null) {
                    Nurse(
                        id = dto.nurseId!!,
                        name = dto.nurseName ?: "Enfermera asignada",
                        specialty = dto.specialty ?: "Enfermería pediátrica"
                    )
                } else null
                _uiState.update { it.copy(hasNurse = hasNurse, nurse = nurse) }
            }
        } catch (_: Exception) { /* mantener estado actual */ }
    }

    private suspend fun loadMotherConsultations(motherId: String) {
        try {
            val response = service.getMotherConsultations(motherId)
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val consultations = dtos.map { dto ->
                    val nurse = Nurse(
                        id = dto.nurseId ?: "",
                        name = dto.nurseName ?: "Enfermera",
                        specialty = "Enfermería pediátrica"
                    )
                    val messages = dto.messages?.map { msg ->
                        Message(
                            id = msg.id,
                            text = msg.content,
                            isFromNurse = msg.senderRole == "NURSE",
                            time = msg.timestamp?.takeLast(5) ?: ""
                        )
                    } ?: emptyList()
                    Consultation(
                        id = dto.id,
                        childId = dto.patientId ?: "",
                        childName = dto.patientName ?: "Paciente",
                        nurse = nurse,
                        isOpen = dto.status == "OPEN",
                        messages = messages
                    )
                }
                _uiState.update { it.copy(consultations = consultations) }
            }
        } catch (_: Exception) { /* mantener estado actual */ }
    }

    // ── Fallback con datos mock ───────────────────────────────────────────────

    private fun loadMockData() {
        loadMockChildren()
        _uiState.update { it.copy(hasNurse = false) }
    }

    private fun loadMockChildren() {
        val children = listOf(
            Child(id = "child-1", name = "Mateo García", age = 3, description = "En tratamiento de anemia"),
            Child(id = "child-2", name = "Lucía García", age = 1, description = "Refuerzo nutricional")
        )
        _uiState.update { it.copy(children = children) }
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    fun getOpenConsultationFor(childId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.childId == childId && it.isOpen }

    fun getConsultationById(consultationId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.id == consultationId }

    fun createConsultation(childId: String, firstMessage: String): String {
        val motherId = tokenManager.userId
        val tempId = UUID.randomUUID().toString()

        viewModelScope.launch {
            if (motherId != null) {
                try {
                    val response = service.startConsultation(
                        StartConsultationRequest(
                            motherId = motherId,
                            patientId = childId,
                            firstMessageContent = firstMessage
                        )
                    )
                    if (response.isSuccessful) {
                        val dto = response.body()!!
                        val nurse = Nurse(
                            id = dto.nurseId ?: "",
                            name = dto.nurseName ?: "Enfermera",
                            specialty = "Enfermería pediátrica"
                        )
                        val child = _uiState.value.children.firstOrNull { it.id == childId }
                        val consultation = Consultation(
                            id = dto.id,
                            childId = childId,
                            childName = child?.name ?: dto.patientName ?: "Paciente",
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
                        _uiState.update {
                            // Reemplazar el temporal si existía
                            val updated = it.consultations.filter { c -> c.id != tempId } + consultation
                            it.copy(consultations = updated)
                        }
                        return@launch
                    }
                } catch (_: Exception) { /* fallback local */ }
            }
            // Fallback: crear consulta local
            createLocalConsultation(childId, firstMessage, tempId)
        }
        return tempId
    }

    private fun createLocalConsultation(childId: String, firstMessage: String, id: String) {
        val child = _uiState.value.children.firstOrNull { it.id == childId }
        val nurse = _uiState.value.nurse ?: Nurse("", "Enfermera asignada", "Enfermería")
        val consultation = Consultation(
            id = id,
            childId = childId,
            childName = child?.name ?: "Paciente",
            nurse = nurse,
            isOpen = true,
            messages = listOf(
                Message(id = UUID.randomUUID().toString(), text = firstMessage, isFromNurse = false, time = currentTime())
            )
        )
        _uiState.update { it.copy(consultations = it.consultations + consultation) }
    }

    // ── Mensajes ──────────────────────────────────────────────────────────────

    fun sendMessage(consultationId: String, text: String) {
        if (text.isBlank()) return
        val senderId = tokenManager.userId ?: return

        // Agregar mensaje optimísticamente
        val newMsg = Message(id = UUID.randomUUID().toString(), text = text.trim(), isFromNurse = false, time = currentTime())
        _uiState.update { state ->
            state.copy(
                consultations = state.consultations.map { c ->
                    if (c.id == consultationId) c.copy(messages = c.messages + newMsg) else c
                }
            )
        }

        viewModelScope.launch {
            try {
                service.sendMessage(
                    SendMessageRequest(
                        consultationId = consultationId,
                        senderId = senderId,
                        senderRole = "MOTHER",
                        content = text.trim()
                    )
                )
            } catch (_: Exception) { /* mensaje ya fue agregado localmente */ }
        }
    }

    fun loadChat(consultationId: String) {
        val requesterId = tokenManager.userId ?: return
        viewModelScope.launch {
            try {
                val response = service.getChat(consultationId, requesterId)
                if (response.isSuccessful) {
                    val messages = response.body()?.messages?.map { msg ->
                        Message(
                            id = msg.id,
                            text = msg.content,
                            isFromNurse = msg.senderRole == "NURSE",
                            time = msg.timestamp?.takeLast(5) ?: ""
                        )
                    } ?: return@launch
                    _uiState.update { state ->
                        state.copy(
                            consultations = state.consultations.map { c ->
                                if (c.id == consultationId) c.copy(messages = messages) else c
                            }
                        )
                    }
                }
            } catch (_: Exception) { /* mantener mensajes actuales */ }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun calculateAge(birthDate: String): Int {
        return try {
            val parts = birthDate.split("-")
            val birthYear = parts[0].toInt()
            val currentYear = java.time.LocalDate.now().year
            currentYear - birthYear
        } catch (_: Exception) { 0 }
    }

    private fun currentTime(): String {
        val now = LocalTime.now()
        return "%02d:%02d".format(now.hour, now.minute)
    }
}
