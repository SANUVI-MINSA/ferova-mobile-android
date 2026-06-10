package pe.edu.upc.ferovafamily.presentation.home

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
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.repository.TreatmentRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.repository.TreatmentRepository

data class ChildInfo(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class HomeUiState(
    val userName: String = "",
    val children: List<ChildInfo> = emptyList(),
    val isLoading: Boolean = false,
    val todayDose: TodayDose? = null,
    val isConfirmingDose: Boolean = false,
    val confirmDoseError: String? = null,
    val confirmDoseSuccess: Boolean = false
)


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager   = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

    private val treatmentRepository: TreatmentRepository = TreatmentRepositoryImpl(
        FerovaApiClient.create(TreatmentApiService::class.java, application)
    )
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        // Nombre del usuario desde TokenManager (guardado al hacer login)
        val name = tokenManager.userName?.takeIf { it.isNotBlank() } ?: "Mamá"
        _uiState.update { it.copy(userName = name, isLoading = true) }

        viewModelScope.launch {
            try {
                // Usar GET /api/patients/my-patients (no requiere motherId en path)
                // Devuelve { motherId, patients: [{id, name}] }
                val response = patientService.getMyPatients()
                if (response.isSuccessful) {
                    val patients = response.body()?.patients ?: emptyList()
                    val children = patients.mapIndexed { index, p ->
                        ChildInfo(
                            id         = p.id,
                            name       = p.name,
                            isSelected = index == 0
                        )
                    }
                    _uiState.update { it.copy(children = children, isLoading = false) }
                    // Cargar dosis del día para el primer hijo seleccionado
                    val selectedChildId = tokenManager.selectedChildId ?: children.firstOrNull()?.id
                    selectedChildId?.let { loadTodayDose(it) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectChild(childId: String) {
        _uiState.update { state ->
            state.copy(children = state.children.map {
                it.copy(isSelected = it.id == childId)
            })
        }
        tokenManager.selectedChildId = childId
        loadTodayDose(childId)
    }

    private fun loadTodayDose(patientId: String) {
        viewModelScope.launch {
            try {
                val todayDose = treatmentRepository.getTodayDose(patientId)

                // 🔥 Verificar si el mensaje indica que no hay tratamiento
                // Esto requiere que el DTO tenga el campo message
                _uiState.update { it.copy(todayDose = todayDose, confirmDoseError = null) }
            } catch (e: Exception) {
                // Si el error es "No active treatment", todayDose = null (tarjeta amarilla)
                val isNoTreatment = e.message?.contains("Treatment has not started") == true ||
                        e.message?.contains("No active") == true

                _uiState.update {
                    it.copy(
                        todayDose = if (isNoTreatment) null else it.todayDose,
                        confirmDoseError = if (!isNoTreatment) e.message else null
                    )
                }
            }
        }
    }

    fun confirmDose(patientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isConfirmingDose = true, confirmDoseError = null, confirmDoseSuccess = false) }
            try {
                val result = treatmentRepository.confirmDose(patientId)
                _uiState.update {
                    it.copy(
                        isConfirmingDose = false,
                        confirmDoseSuccess = true
                    )
                }
                // Recargar la dosis del día después de confirmar
                loadTodayDose(patientId)
                // Limpiar el mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(confirmDoseSuccess = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isConfirmingDose = false,
                        confirmDoseError = e.message
                    )
                }
            }
        }
    }

    fun clearConfirmDoseError() {
        _uiState.update { it.copy(confirmDoseError = null) }
    }

}