package pe.edu.upc.ferovafamily.presentation.nutritional_diary

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
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService

data class NutritionalDiaryUiState(
    val patients: List<String> = emptyList(),
    val selectedPatient: String = "",
    val isLoading: Boolean = false
)

class NutritionalDiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager   = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

    private val _uiState = MutableStateFlow(NutritionalDiaryUiState())
    val uiState: StateFlow<NutritionalDiaryUiState> = _uiState.asStateFlow()

    init {
        loadPatients()
    }

    private fun loadPatients() {
        val motherId = tokenManager.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = patientService.getPatientsByMother(motherId)
                if (response.isSuccessful) {
                    val names = response.body()?.map { it.name } ?: emptyList()
                    _uiState.update {
                        it.copy(
                            patients = names,
                            selectedPatient = names.firstOrNull() ?: ""
                        )
                    }
                }
            } catch (_: Exception) {
                // Sin conexión: dejar lista vacía
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectPatient(name: String) {
        _uiState.update { it.copy(selectedPatient = name) }
    }
}
