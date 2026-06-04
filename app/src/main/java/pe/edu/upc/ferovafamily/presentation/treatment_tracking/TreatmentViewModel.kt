package pe.edu.upc.ferovafamily.presentation.treatment_tracking

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
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.repository.TreatmentRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.repository.TreatmentRepository

data class TreatmentUiState(
    val todayDose: TodayDose? = null,
    val doseHistory: List<DoseRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isConfirming: Boolean = false,
    val doseConfirmed: Boolean = false,
    val errorMessage: String? = null
)

class TreatmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TreatmentRepository = TreatmentRepositoryImpl(
        FerovaApiClient.create(TreatmentApiService::class.java, application)
    )
    private val tokenManager = TokenManager.getInstance(application)

    private val _uiState = MutableStateFlow(TreatmentUiState())
    val uiState: StateFlow<TreatmentUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val patientId = tokenManager.userId ?: "default"
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val todayDose   = repository.getTodayDose(patientId)
                val doseHistory = repository.getDoseHistory(patientId)
                _uiState.update {
                    it.copy(
                        todayDose   = todayDose,
                        doseHistory = doseHistory,
                        isLoading   = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    fun confirmDose() {
        val patientId = tokenManager.userId ?: return
        _uiState.update { it.copy(isConfirming = true) }

        viewModelScope.launch {
            try {
                val record = repository.confirmDose(patientId)
                _uiState.update { state ->
                    state.copy(
                        isConfirming  = false,
                        doseConfirmed = true,
                        todayDose     = state.todayDose?.copy(canConfirm = false, confirmedAt = record.confirmedAt),
                        doseHistory   = listOf(record) + state.doseHistory
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isConfirming = false, errorMessage = e.message)
                }
            }
        }
    }

    fun clearError()        = _uiState.update { it.copy(errorMessage = null) }
    fun clearDoseConfirmed()= _uiState.update { it.copy(doseConfirmed = false) }

    /** Agrupa el historial por fecha para mostrar secciones en la UI */
    val doseHistoryGrouped: Map<String, List<DoseRecord>>
        get() = _uiState.value.doseHistory.groupBy { it.date }
}
