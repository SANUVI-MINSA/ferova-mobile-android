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
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.NutritionalDiaryApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.repository.NutritionalDiaryRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.nutrition.*
import pe.edu.upc.ferovafamily.domain.repository.NutritionalDiaryRepository

class NutritionalDiaryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val tokenManager   = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

    private val _uiState = MutableStateFlow(NutritionalDiaryUiState())
    val uiState: StateFlow<NutritionalDiaryUiState> = _uiState.asStateFlow()


    // ════════════════════════════════════════════════════════════════════════
    // REPOSITORY (inyectado con el servicio API)
    // ════════════════════════════════════════════════════════════════════════

    private val apiService = FerovaApiClient.create(
        NutritionalDiaryApiService::class.java,
        application
    )
    private val repository: NutritionalDiaryRepository = NutritionalDiaryRepositoryImpl(apiService)

    // ════════════════════════════════════════════════════════════════════════
    // STATE FLOWS
    // ════════════════════════════════════════════════════════════════════════

    // ── Registrar Alimento ──
    private val _registerFoodEntryResult = MutableStateFlow<RegisterFoodEntryResult?>(null)
    val registerFoodEntryResult: StateFlow<RegisterFoodEntryResult?> = _registerFoodEntryResult.asStateFlow()

    // ── Diario de Hoy ──
    private val _todayDiary = MutableStateFlow<TodayDiary?>(null)
    val todayDiary: StateFlow<TodayDiary?> = _todayDiary.asStateFlow()

    // ── Historial Nutricional ──
    private val _nutritionalHistory = MutableStateFlow<NutritionalHistory?>(null)
    val nutritionalHistory: StateFlow<NutritionalHistory?> = _nutritionalHistory.asStateFlow()

    // ── Alimentos por Categoría ──
    private val _foodsByCategory = MutableStateFlow<CategoryFood?>(null)
    val foodsByCategory: StateFlow<CategoryFood?> = _foodsByCategory.asStateFlow()

    // ── Búsqueda de Alimentos ──
    private val _searchFoodResult = MutableStateFlow<SearchFoodResult?>(null)
    val searchFoodResult: StateFlow<SearchFoodResult?> = _searchFoodResult.asStateFlow()

    // ── Detalles de Alimento ──
    private val _foodItemDetails = MutableStateFlow<FoodItemDetails?>(null)
    val foodItemDetails: StateFlow<FoodItemDetails?> = _foodItemDetails.asStateFlow()

    // ── Estados de Carga y Error ──
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    // ════════════════════════════════════════════════════════════════════════
    // FUNCIONES PÚBLICAS (llamadas desde UI)
    // ════════════════════════════════════════════════════════════════════════

    init {
        loadPatients()
    }

    private fun loadPatients() {
        // Ya no necesitas motherId para este endpoint, pero puedes mantenerlo si lo usas para logs

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = patientService.getMyPatients()

                println("Code: ${response.code()}")
                println("Body: ${response.body()}")
                println("Patients: ${response.body()?.patients?.size}")

                if (response.isSuccessful) {
                    // ACCESO CORRECTO: response.body()?.patients
                    val patientItems = response.body()?.patients?.map {
                        it.toDomain()
                    } ?: emptyList()

                    _uiState.update {
                        it.copy(
                            patients = patientItems,
                            selectedPatient = patientItems.firstOrNull()
                        )
                    }

                    println("UI Patients: ${_uiState.value.patients.size}")
                    println("UI Selected: ${_uiState.value.selectedPatient?.name}")

                    // Cargar diario del primer paciente automáticamente si existe
                    patientItems.firstOrNull()?.let {
                        loadTodayDiary(it.id)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectPatient(patientId: String) {
        _uiState.update { state ->
            val selectedPatient = state.patients.find { it.id == patientId }
            state.copy(selectedPatient = selectedPatient)
        }
    }

    /**
     * Registra el consumo de un alimento
     */
    fun registerFoodEntry(patientId: String, foodItemId: String, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _warning.value = null

            try {
                val result = repository.registerFoodEntry(patientId, foodItemId, quantity)
                _registerFoodEntryResult.value = result

                // Si hay advertencia, mostrarla
                result.warningMessage?.let { _warning.value = it }

                // Recargar el diario de hoy
                loadTodayDiary(patientId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene el diario nutricional del día actual
     */
    fun loadTodayDiary(patientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val diary = repository.getTodayDiary(patientId)
                _todayDiary.value = diary
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar diario de hoy"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene el historial nutricional
     */
    fun loadNutritionalHistory(
        patientId: String,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val history = repository.getNutritionalHistory(patientId, startDate, endDate)
                _nutritionalHistory.value = history
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar historial nutricional"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene alimentos por categoría
     */
    fun loadFoodsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val foods = repository.getFoodsByCategory(category)
                _foodsByCategory.value = foods
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar alimentos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Busca alimentos por nombre
     */
    fun searchFoods(searchText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = repository.searchFoods(searchText)
                _searchFoodResult.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Error en la búsqueda"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene detalles de un alimento específico
     */
    fun loadFoodItemDetails(foodItemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val details = repository.getFoodDetail(foodItemId)
                _foodItemDetails.value = details

                // Si es inhibidor, mostrar advertencia
                details.warningMessage?.let { _warning.value = it }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar detalles del alimento"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el resultado del último registro (evita que el diálogo se cierre
     * solo al reabrirlo cuando el ViewModel es compartido entre pantallas).
     */
    fun clearRegisterResult() {
        _registerFoodEntryResult.value = null
    }

    /**
     * Limpia los errores
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Limpia las advertencias
     */
    fun clearWarning() {
        _warning.value = null
    }
}



data class NutritionalDiaryUiState(
    val patients: List<Patient> = emptyList(),
    val selectedPatient: Patient? = null,
    val isLoading: Boolean = false
)





