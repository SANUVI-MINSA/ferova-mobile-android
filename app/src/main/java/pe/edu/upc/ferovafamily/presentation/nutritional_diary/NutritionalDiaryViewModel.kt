package pe.edu.upc.ferovafamily.presentation.nutritional_diary

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
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.NutritionalDiaryApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.repository.NutritionalDiaryRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.domain.model.nutrition.*
import pe.edu.upc.ferovafamily.domain.repository.NutritionalDiaryRepository

private const val TAG = "NutritionalDiaryVM"

class NutritionalDiaryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)

    private val _uiState = MutableStateFlow(NutritionalDiaryUiState())
    val uiState: StateFlow<NutritionalDiaryUiState> = _uiState.asStateFlow()

    private val apiService = FerovaApiClient.create(
        NutritionalDiaryApiService::class.java,
        application
    )
    private val repository: NutritionalDiaryRepository = NutritionalDiaryRepositoryImpl(apiService)

    // ── State Flows ──
    private val _registerFoodEntryResult = MutableStateFlow<RegisterFoodEntryResult?>(null)
    val registerFoodEntryResult: StateFlow<RegisterFoodEntryResult?> = _registerFoodEntryResult.asStateFlow()

    private val _todayDiary = MutableStateFlow<TodayDiary?>(null)
    val todayDiary: StateFlow<TodayDiary?> = _todayDiary.asStateFlow()

    private val _nutritionalHistory = MutableStateFlow<NutritionalHistory?>(null)
    val nutritionalHistory: StateFlow<NutritionalHistory?> = _nutritionalHistory.asStateFlow()

    private val _foodsByCategory = MutableStateFlow<CategoryFood?>(null)
    val foodsByCategory: StateFlow<CategoryFood?> = _foodsByCategory.asStateFlow()

    private val _searchFoodResult = MutableStateFlow<SearchFoodResult?>(null)
    val searchFoodResult: StateFlow<SearchFoodResult?> = _searchFoodResult.asStateFlow()

    private val _foodItemDetails = MutableStateFlow<FoodItemDetails?>(null)
    val foodItemDetails: StateFlow<FoodItemDetails?> = _foodItemDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    init {
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = patientService.getMyPatients()

                Log.d(TAG, "loadPatients - Code: ${response.code()}")
                Log.d(TAG, "loadPatients - Body: ${response.body()}")

                if (response.isSuccessful) {
                    val patientItems = response.body()?.patients?.map {
                        it.toDomain()
                    } ?: emptyList()

                    Log.d(TAG, "loadPatients - Pacientes cargados: ${patientItems.size}")

                    val currentSelectedId = _uiState.value.selectedPatient?.id
                    val selectedPatient = if (currentSelectedId != null) {
                        patientItems.find { it.id == currentSelectedId }
                            ?: patientItems.firstOrNull()
                    } else {
                        patientItems.firstOrNull()
                    }

                    _uiState.update {
                        it.copy(
                            patients = patientItems,
                            selectedPatient = selectedPatient
                        )
                    }

                    Log.d(TAG, "loadPatients - Paciente seleccionado: ${selectedPatient?.name} (ID: ${selectedPatient?.id})")

                    selectedPatient?.let {
                        loadTodayDiary(it.id)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e(TAG, "loadPatients - Error: ${e.message}", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshPatients() {
        Log.d(TAG, "🔄 refreshPatients - Refrescando pacientes...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = patientService.getMyPatients()

                if (response.isSuccessful) {
                    val patientItems = response.body()?.patients?.map {
                        it.toDomain()
                    } ?: emptyList()

                    Log.d(TAG, "refreshPatients - Pacientes refrescados: ${patientItems.size}")

                    val selectedPatient = patientItems.firstOrNull()

                    _uiState.update {
                        it.copy(
                            patients = patientItems,
                            selectedPatient = selectedPatient
                        )
                    }

                    Log.d(TAG, "refreshPatients - Paciente seleccionado: ${selectedPatient?.name} (ID: ${selectedPatient?.id})")

                    selectedPatient?.let {
                        loadTodayDiary(it.id)
                    }
                } else {
                    _error.value = "Error al refrescar pacientes: ${response.code()}"
                    Log.e(TAG, "refreshPatients - Error response: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e(TAG, "refreshPatients - Error: ${e.message}", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectPatient(patientId: String) {
        Log.d(TAG, "selectPatient - Seleccionando paciente ID: $patientId")
        _uiState.update { state ->
            val selectedPatient = state.patients.find { it.id == patientId }
            Log.d(TAG, "selectPatient - Paciente encontrado: ${selectedPatient?.name}")
            state.copy(selectedPatient = selectedPatient)
        }
    }

    /**
     * Registra el consumo de un alimento - CON LOGS DETALLADOS
     */
    fun registerFoodEntry(patientId: String, foodItemId: String, quantity: Int) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "📝 registerFoodEntry - INICIO")
        Log.d(TAG, "📝 patientId: $patientId")
        Log.d(TAG, "📝 foodItemId: $foodItemId")
        Log.d(TAG, "📝 quantity: $quantity")
        Log.d(TAG, "========================================")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _warning.value = null

            try {
                Log.d(TAG, "📤 Llamando a repository.registerFoodEntry...")

                val result = repository.registerFoodEntry(patientId, foodItemId, quantity)

                Log.d(TAG, "✅ Resultado del repositorio:")
                Log.d(TAG, "   - success: ${result.success}")
                Log.d(TAG, "   - message: ${result.message}")
                Log.d(TAG, "   - newTotalIronAbsorbed: ${result.newTotalIronAbsorbed}")
                Log.d(TAG, "   - warningMessage: ${result.warningMessage}")
                Log.d(TAG, "   - foodEntry: ${result.foodEntry}")

                _registerFoodEntryResult.value = result

                result.warningMessage?.let { _warning.value = it }

                Log.d(TAG, "🔄 Recargando diario para patientId: $patientId")
                loadTodayDiary(patientId)

                Log.d(TAG, "✅ registerFoodEntry - COMPLETADO EXITOSAMENTE")
                Log.d(TAG, "========================================")

            } catch (e: Exception) {
                Log.e(TAG, "❌ registerFoodEntry - ERROR: ${e.message}", e)
                _error.value = e.message ?: "Error desconocido"
                Log.d(TAG, "========================================")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene el diario nutricional del día actual - CON LOGS
     */
    fun loadTodayDiary(patientId: String) {
        Log.d(TAG, "loadTodayDiary - Cargando diario para patientId: $patientId")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val diary = repository.getTodayDiary(patientId)
                _todayDiary.value = diary
                Log.d(TAG, "loadTodayDiary - Diario cargado: ${diary.foodEntries.size} alimentos, totalFe: ${diary.totalIronAbsorbed}")
            } catch (e: Exception) {
                Log.e(TAG, "loadTodayDiary - Error: ${e.message}", e)
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
                details.warningMessage?.let { _warning.value = it }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar detalles del alimento"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRegisterResult() {
        _registerFoodEntryResult.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearWarning() {
        _warning.value = null
    }

    fun clearData() {
        Log.d(TAG, "clearData - Limpiando todos los datos")
        _uiState.update { it.copy(patients = emptyList(), selectedPatient = null) }
        _todayDiary.value = null
        _nutritionalHistory.value = null
        _foodsByCategory.value = null
        _searchFoodResult.value = null
        _foodItemDetails.value = null
        _registerFoodEntryResult.value = null
        _error.value = null
        _warning.value = null
    }
}

data class NutritionalDiaryUiState(
    val patients: List<Patient> = emptyList(),
    val selectedPatient: Patient? = null,
    val isLoading: Boolean = false
)