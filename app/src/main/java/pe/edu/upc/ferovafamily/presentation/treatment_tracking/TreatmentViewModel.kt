package pe.edu.upc.ferovafamily.presentation.treatment_tracking

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
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.repository.TreatmentRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.DoseRecord
import pe.edu.upc.ferovafamily.domain.repository.TreatmentRepository
import java.time.LocalDateTime
import java.util.Locale

private const val TAG = "TreatmentViewModel"

data class TreatmentUiState(
    val patientName: String = "",
    val supplementName: String = "",
    val quantity: String = "",
    val dosingHours: String = "",
    val doseHistory: List<DoseRecord> = emptyList(),
    val isLoading: Boolean = false,
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
        val patientId = tokenManager.selectedChildId ?: tokenManager.userId

        Log.d(TAG, "========== loadData ==========")
        Log.d(TAG, "selectedChildId: ${tokenManager.selectedChildId}")
        Log.d(TAG, "userId: ${tokenManager.userId}")
        Log.d(TAG, "Final patientId: $patientId")

        if (patientId.isNullOrBlank()) {
            Log.e(TAG, "No patientId found")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {

                Log.d(TAG, "Calling repository.getDoseHistory...")

                val history = repository.getDoseHistory(patientId)

                Log.d(TAG, "Repository returned successfully")
                Log.d(TAG, "Patient: ${history.patientName}")
                Log.d(TAG, "Doses received: ${history.doses.size}")

                _uiState.update {
                    it.copy(
                        patientName = history.patientName,
                        supplementName = history.supplementName,
                        quantity = history.quantity,
                        dosingHours = history.dosingHours,
                        doseHistory = history.doses,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                Log.d(TAG, "UI State updated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading dose history", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    val doseHistoryGrouped: Map<String, List<DoseRecord>>
        get() = _uiState.value.doseHistory.groupBy { formatDate(it.scheduledDate) }

    private fun formatDate(isoDate: String): String {
        return try {
            val parsed = LocalDateTime.parse(isoDate)
            "${parsed.dayOfMonth} ${parsed.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale("es"))}"
        } catch (e: Exception) {
            isoDate.substring(0, 10)
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}