package pe.edu.upc.ferovafamily.presentation.progress

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.AchievementApiService
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.repository.AchievementRepositoryImpl
import pe.edu.upc.ferovafamily.data.repository.PatientRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.Badge
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository
import pe.edu.upc.ferovafamily.domain.repository.PatientRepository
import pe.edu.upc.ferovafamily.presentation.progress.model.HemoglobinPoint
import pe.edu.upc.ferovafamily.presentation.progress.model.Medal
import pe.edu.upc.ferovafamily.presentation.progress.model.MedalType
import pe.edu.upc.ferovafamily.presentation.progress.model.ProgressStats

private const val TAG = "ProgressViewModel"

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val achievementRepository: AchievementRepository = AchievementRepositoryImpl(
        FerovaApiClient.create(AchievementApiService::class.java, application)
    )
    private val patientRepository: PatientRepository = PatientRepositoryImpl(
        FerovaApiClient.create(PatientApiService::class.java, application)
    )
    private val tokenManager = TokenManager.getInstance(application)

    private val _state = MutableStateFlow(ProgressStats())
    val state: StateFlow<ProgressStats> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val patientId = tokenManager.selectedChildId ?: tokenManager.userId

        Log.d(TAG, "loadData - patientId: $patientId")
        Log.d(TAG, "selectedChildId: ${tokenManager.selectedChildId}")
        Log.d(TAG, "userId: ${tokenManager.userId}")

        if (patientId.isNullOrBlank()) {
            Log.e(TAG, "patientId is null or blank, loading mock data")
            loadMockData()
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Fetching achievement progress...")
                val progress = achievementRepository.getAchievementProgress(patientId)
                Log.d(TAG, "Progress received: points=${progress.points}, streak=${progress.currentStreak}")

                Log.d(TAG, "Fetching badges...")
                val badges = achievementRepository.getBadges(patientId)
                Log.d(TAG, "Badges received: ${badges.size}")

                Log.d(TAG, "Fetching hemoglobin evolution...")
                val hemoglobin = patientRepository.getHemoglobinEvolution(patientId)
                Log.d(TAG, "Hemoglobin records: ${hemoglobin.size}")

                // Convertir datos
                val hemoglobinPoints = hemoglobin.map { record ->
                    HemoglobinPoint(record.date, record.value)
                }
                val lastHemoglobin = hemoglobin.lastOrNull()?.value ?: 0f

                // ✅ ACTUALIZAR STATE
                _state.value = ProgressStats(
                    healthStatus = progress.healthStatus,
                    totalPoints = progress.points,
                    currentStreak = progress.currentStreak,
                    longestStreak = progress.bestStreak,
                    currentHemoglobin = lastHemoglobin,
                    hemoglobinHistory = hemoglobinPoints,
                    medals = buildMedalsFromBadges(badges, progress.currentStreak)
                )

                Log.d(TAG, "State updated - points: ${_state.value.totalPoints}, medals: ${_state.value.medals.size}")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
                loadMockData()
            }
        }
    }

    private fun buildMedalsFromBadges(
        badges: List<Badge>,
        currentStreak: Int
    ): List<Medal> {
        if (badges.isEmpty()) {
            return buildDefaultMedals(currentStreak)
        }

        return badges.map { badge ->
            // 🔥 Usar el mayor entre el progress del badge y el currentStreak real
            val actualProgress = maxOf(badge.currentProgress, currentStreak)

            Medal(
                id = badge.id,
                type = when (badge.category) {
                    "FIRST_WEEK" -> MedalType.FIRST_WEEK
                    "HALF_TREATMENT" -> MedalType.HALF_TREATMENT
                    "TREATMENT_COMPLETED" -> MedalType.TREATMENT_COMPLETED
                    else -> MedalType.FIRST_WEEK
                },
                title = badge.name,
                description = badge.description,
                isUnlocked = badge.isUnlocked || actualProgress >= badge.targetProgress,  // 🔥 Forzar desbloqueo si cumple
                currentDays = actualProgress,
                targetDays = badge.targetProgress,
                celebrationMessage = "¡${badge.name} desbloqueada!"
            )
        }
    }

    private fun buildDefaultMedals(currentStreak: Int): List<Medal> = listOf(
        Medal("first-week", MedalType.FIRST_WEEK, "Primera semana", "Completa 7 días sin fallar",
            currentStreak >= 7, minOf(currentStreak, 7), 7, "¡7 días completados!"),
        Medal("half-treatment", MedalType.HALF_TREATMENT, "Mitad del tratamiento", "Completa 45 días",
            currentStreak >= 45, minOf(currentStreak, 45), 45, "¡Mitad del camino!"),
        Medal("treatment-completed", MedalType.TREATMENT_COMPLETED, "Tratamiento completo", "Completa 90 días",
            currentStreak >= 90, minOf(currentStreak, 90), 90, "¡Tratamiento completo!"),
    )

    private fun loadMockData() {
        _state.value = ProgressStats(
            healthStatus = "Activo",
            totalPoints = 20,
            currentStreak = 2,
            longestStreak = 2,
            currentHemoglobin = 11.2f,
            hemoglobinHistory = listOf(
                HemoglobinPoint("12 Abril", 7.0f),
                HemoglobinPoint("14 Mayo", 8.0f),
                HemoglobinPoint("12 Junio", 9.0f),
                HemoglobinPoint("28 Julio", 11.2f)
            ),
            medals = buildDefaultMedals(2)
        )
    }

    fun getMedalById(id: String): Medal? =
        _state.value.medals.firstOrNull { it.id == id }
}