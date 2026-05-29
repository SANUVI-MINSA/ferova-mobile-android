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

    private val tokenManager  = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        // Nombre del usuario (guardado en TokenManager al hacer login)
        val name = tokenManager.userName?.takeIf { it.isNotBlank() } ?: "Mamá"
        _uiState.update { it.copy(userName = name) }

        val motherId = tokenManager.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = patientService.getPatientsByMother(motherId)
                if (response.isSuccessful) {
                    val patients = response.body() ?: emptyList()
                    val children = patients.mapIndexed { index, p ->
                        ChildInfo(
                            id = p.id,
                            name = p.name,
                            isSelected = index == 0
                        )
                    }
                    _uiState.update { it.copy(children = children) }
                }
            } catch (_: Exception) {
                // Sin conexión o error: dejar lista vacía, la UI mostrará solo el botón "+"
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectChild(childId: String) {
        _uiState.update { state ->
            state.copy(children = state.children.map { it.copy(isSelected = it.id == childId) })
        }
    }

    val selectedChildId: String?
        get() = _uiState.value.children.firstOrNull { it.isSelected }?.id
}
