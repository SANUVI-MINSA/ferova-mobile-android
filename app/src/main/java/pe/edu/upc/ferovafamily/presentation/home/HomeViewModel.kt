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

data class ChildInfo(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class HomeUiState(
    val userName: String = "",
    val children: List<ChildInfo> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager   = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

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
    }

}
