package pe.edu.upc.ferovafamily.presentation.home

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
import pe.edu.upc.ferovafamily.data.remote.api.AchievementApiService
import pe.edu.upc.ferovafamily.data.remote.api.NutritionalDiaryApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.api.TreatmentApiService
import pe.edu.upc.ferovafamily.data.repository.AchievementRepositoryImpl
import pe.edu.upc.ferovafamily.data.repository.NutritionalDiaryRepositoryImpl
import pe.edu.upc.ferovafamily.data.repository.TreatmentRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.TodayDose
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository
import pe.edu.upc.ferovafamily.domain.repository.NutritionalDiaryRepository
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
    val confirmDoseSuccess: Boolean = false,
    //  CAMPOS PARA LOGROS
    val currentStreak: Int = 0,
    val totalPoints: Int = 0,
    //  CAMPO PARA NUTRICIÓN
    val ironAbsorbedToday: Double = 0.0
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val patientService = FerovaApiClient.create(PatientApiService::class.java, application)
    private val achievementRepository: AchievementRepository = AchievementRepositoryImpl(
        FerovaApiClient.create(AchievementApiService::class.java, application)
    )
    private val treatmentRepository: TreatmentRepository = TreatmentRepositoryImpl(
        FerovaApiClient.create(TreatmentApiService::class.java, application)
    )
    private val nutritionRepository: NutritionalDiaryRepository = NutritionalDiaryRepositoryImpl(
        FerovaApiClient.create(NutritionalDiaryApiService::class.java, application)
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        val name = tokenManager.userName?.takeIf { it.isNotBlank() } ?: "Mamá"
        _uiState.update { it.copy(userName = name, isLoading = true) }

        viewModelScope.launch {
            try {
                val response = patientService.getMyPatients()
                if (response.isSuccessful) {
                    val patients = response.body()?.patients ?: emptyList()
                    val children = patients.mapIndexed { index, p ->
                        ChildInfo(
                            id = p.id,
                            name = p.name,
                            isSelected = index == 0
                        )
                    }
                    _uiState.update { it.copy(children = children, isLoading = false) }
                    val selectedChildId = tokenManager.selectedChildId ?: children.firstOrNull()?.id
                    selectedChildId?.let {
                        loadTodayDose(it)
                        loadAchievements(it)
                        loadIron(it)
                    }
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
        loadAchievements(childId)
        loadIron(childId)
    }

    // Cargar hierro absorbido hoy del paciente seleccionado
    private fun loadIron(patientId: String) {
        viewModelScope.launch {
            try {
                val diary = nutritionRepository.getTodayDiary(patientId)
                _uiState.update { it.copy(ironAbsorbedToday = diary.totalIronAbsorbed) }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading iron", e)
                _uiState.update { it.copy(ironAbsorbedToday = 0.0) }
            }
        }
    }

    private fun loadTodayDose(patientId: String) {
        viewModelScope.launch {
            try {
                val todayDose = treatmentRepository.getTodayDose(patientId)
                _uiState.update { it.copy(todayDose = todayDose, confirmDoseError = null) }
            } catch (e: Exception) {
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

    // NUEVA FUNCIÓN: Cargar logros (rachas y puntos)
    private fun loadAchievements(patientId: String) {
        viewModelScope.launch {
            try {
                val progress = achievementRepository.getAchievementProgress(patientId)
                _uiState.update {
                    it.copy(
                        currentStreak = progress.currentStreak,
                        totalPoints = progress.points
                    )
                }
                Log.d("HomeViewModel", "Achievements loaded - streak: ${progress.currentStreak}, points: ${progress.points}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading achievements", e)
                _uiState.update {
                    it.copy(
                        currentStreak = 0,
                        totalPoints = 0
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
                loadTodayDose(patientId)
                loadAchievements(patientId)  // Recargar logros después de confirmar
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