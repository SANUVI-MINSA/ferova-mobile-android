package pe.edu.upc.ferovafamily.presentation.consultations

import android.app.Application
import android.util.Log
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
import pe.edu.upc.ferovafamily.data.repository.ConsultationRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.communication.Consultation
import pe.edu.upc.ferovafamily.domain.model.communication.Message
import pe.edu.upc.ferovafamily.domain.model.communication.PatientWithNurse
import pe.edu.upc.ferovafamily.domain.repository.ConsultationRepository
import java.time.LocalTime
import java.util.UUID

private const val TAG = "ConsultationsVM"

data class ConsultationsUiState(
    val patients: List<PatientWithNurse> = emptyList(),
    val consultations: List<Consultation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    /** Id de la consulta recién creada; la pantalla lo consume para navegar al chat. */
    val createdConsultationId: String? = null
) {
    /** Hay al menos un hijo con enfermera asignada → se habilita "Nueva Consulta". */
    val hasNurse: Boolean get() = patients.any { it.hasNurse }
}

class ConsultationsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val repository: ConsultationRepository = ConsultationRepositoryImpl(
        FerovaApiClient.create(ConsultationApiService::class.java, application)
    )

    private val _uiState = MutableStateFlow(ConsultationsUiState())
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    // ── Carga inicial ─────────────────────────────────────────────────────────

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val patients = repository.getPatientsWithNurse()
                val consultations = repository.getMotherConsultations()
                _uiState.update {
                    it.copy(patients = patients, consultations = consultations, isLoading = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadData error", e)
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error al cargar consultas")
                }
            }
        }
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    fun getPatientById(patientId: String): PatientWithNurse? =
        _uiState.value.patients.firstOrNull { it.patientId == patientId }

    fun getOpenConsultationFor(patientId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.patientId == patientId && it.isOpen }

    fun getConsultationById(consultationId: String): Consultation? =
        _uiState.value.consultations.firstOrNull { it.id == consultationId }

    fun createConsultation(patientId: String, firstMessage: String) {
        if (firstMessage.isBlank()) return
        viewModelScope.launch {
            try {
                val consultation = repository.startConsultation(patientId, firstMessage)
                _uiState.update { state ->
                    val merged = state.consultations.filter { it.id != consultation.id } + consultation
                    state.copy(consultations = merged, createdConsultationId = consultation.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "createConsultation error", e)
                _uiState.update { it.copy(error = e.message ?: "No se pudo iniciar la consulta") }
            }
        }
    }

    /** La pantalla llama esto tras navegar, para no repetir la navegación. */
    fun consumeCreatedConsultation() {
        _uiState.update { it.copy(createdConsultationId = null) }
    }

    // ── Mensajes ──────────────────────────────────────────────────────────────

    fun sendMessage(consultationId: String, text: String) {
        if (text.isBlank()) return

        // Optimista: lo agregamos local y luego sincronizamos con el backend.
        val optimistic = Message(
            id = UUID.randomUUID().toString(),
            text = text.trim(),
            isFromNurse = false,
            time = currentTime()
        )
        _uiState.update { state ->
            state.copy(consultations = state.consultations.map { c ->
                if (c.id == consultationId) c.copy(messages = c.messages + optimistic) else c
            })
        }

        viewModelScope.launch {
            try {
                repository.sendMessage(consultationId, text.trim())
                loadChat(consultationId)
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage error", e)
            }
        }
    }

    fun loadChat(consultationId: String) {
        viewModelScope.launch {
            try {
                val messages = repository.getChat(consultationId)
                if (messages.isEmpty()) return@launch
                _uiState.update { state ->
                    state.copy(consultations = state.consultations.map { c ->
                        if (c.id == consultationId) c.copy(messages = messages) else c
                    })
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadChat error", e)
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentTime(): String {
        val now = LocalTime.now()
        return "%02d:%02d".format(now.hour, now.minute)
    }
}
