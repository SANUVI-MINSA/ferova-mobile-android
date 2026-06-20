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
    val createdConsultationId: String? = null
) {
    val hasNurse: Boolean get() = patients.any { it.hasNurse }
}

class ConsultationsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val repository: ConsultationRepository = ConsultationRepositoryImpl(
        context = application,
        service = FerovaApiClient.create(ConsultationApiService::class.java, application)
    )

    private val _uiState = MutableStateFlow(ConsultationsUiState())
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val patients = repository.getPatientsWithNurse()
                Log.d(TAG, "loadData: patients size=${patients.size}")

                val consultations = repository.getMotherConsultations()
                Log.d(TAG, "loadData: consultations size=${consultations.size}")

                _uiState.update { state ->
                    val mergedConsultations = consultations.map { newConsultation ->
                        val existing = state.consultations.firstOrNull { it.id == newConsultation.id }
                        if (existing != null) {
                            newConsultation.copy(messages = existing.messages)
                        } else {
                            newConsultation
                        }
                    }

                    state.copy(
                        patients = patients,
                        consultations = mergedConsultations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadData error", e)
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error al cargar consultas")
                }
            }
        }
    }

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
                    val merged = (state.consultations + consultation).distinctBy { it.id }
                    state.copy(consultations = merged, createdConsultationId = consultation.id)
                }
                loadData()
            } catch (e: Exception) {
                Log.e(TAG, "createConsultation error", e)
                _uiState.update { it.copy(error = e.message ?: "No se pudo iniciar la consulta") }
            }
        }
    }

    fun consumeCreatedConsultation() {
        _uiState.update { it.copy(createdConsultationId = null) }
    }

    fun sendMessage(consultationId: String, text: String) {
        if (text.isBlank()) return

        val consultation = _uiState.value.consultations.firstOrNull { it.id == consultationId }
        if (consultation == null) {
            Log.e(TAG, "sendMessage: Consulta no encontrada")
            _uiState.update { it.copy(error = "Consulta no encontrada") }
            return
        }

        if (!consultation.isOpen) {
            Log.e(TAG, "sendMessage: Consulta cerrada")
            _uiState.update { it.copy(error = "Esta consulta está cerrada") }
            return
        }

        val optimistic = Message(
            id = UUID.randomUUID().toString(),
            text = text.trim(),
            isFromNurse = false,
            time = currentTime()
        )

        _uiState.update { state ->
            state.copy(consultations = state.consultations.map { c ->
                if (c.id == consultationId) {
                    c.copy(messages = c.messages + optimistic)
                } else c
            })
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "sendMessage: Enviando mensaje a consulta $consultationId")
                repository.sendMessage(consultationId, text.trim())
                Log.d(TAG, "sendMessage: Mensaje enviado correctamente")
                loadChat(consultationId)
                loadData()
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage error", e)
                _uiState.update { state ->
                    state.copy(consultations = state.consultations.map { c ->
                        if (c.id == consultationId) {
                            val messagesWithoutOptimistic = c.messages.filter { it.id != optimistic.id }
                            c.copy(messages = messagesWithoutOptimistic)
                        } else c
                    })
                }
                _uiState.update {
                    it.copy(error = e.message ?: "No se pudo enviar el mensaje")
                }
            }
        }
    }

    fun loadChat(consultationId: String) {
        viewModelScope.launch {
            try {
                val messages = repository.getChat(consultationId)
                Log.d(TAG, "loadChat: messages size=${messages.size}")

                _uiState.update { state ->
                    val existingConsultation = state.consultations.firstOrNull { it.id == consultationId }

                    if (existingConsultation != null) {
                        // ✅ Si la consulta existe, SOLO actualizar mensajes
                        state.copy(
                            consultations = state.consultations.map { c ->
                                if (c.id == consultationId) {
                                    c.copy(messages = messages)
                                } else c
                            }
                        )
                    } else {
                        // ✅ Si no existe en el estado, mantener estado (NO mostrar diálogo)
                        state
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadChat error", e)
                // ✅ NO ELIMINAR LA CONSULTA EN CASO DE ERROR
            }
        }
    }

    private fun currentTime(): String {
        val now = LocalTime.now()
        return "%02d:%02d".format(now.hour, now.minute)
    }
}