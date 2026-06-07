package pe.edu.upc.ferovafamily.presentation.progress

import android.app.Application
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
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository
import pe.edu.upc.ferovafamily.domain.repository.PatientRepository
import pe.edu.upc.ferovafamily.presentation.progress.model.HemoglobinPoint
import pe.edu.upc.ferovafamily.presentation.progress.model.Medal
import pe.edu.upc.ferovafamily.presentation.progress.model.MedalType
import pe.edu.upc.ferovafamily.presentation.progress.model.ProgressStats

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
        val patientId = tokenManager.userId ?: run { loadMockData(); return }

        viewModelScope.launch {
            try {
                // Llamadas secuenciales (evita problemas de scope con async)
                val progress   = achievementRepository.getAchievementProgress(patientId)
                val hemoglobin = patientRepository.getHemoglobinEvolution(patientId)

                val hemoglobinPoints = hemoglobin.map { record ->
                    HemoglobinPoint(record.date, record.value)
                }
                val lastHemoglobin = hemoglobin.lastOrNull()?.value ?: 0f

                _state.value = ProgressStats(
                    healthStatus      = progress.healthStatus,
                    totalPoints       = progress.points,
                    currentStreak     = progress.currentStreak,
                    longestStreak     = progress.bestStreak,
                    currentHemoglobin = lastHemoglobin,
                    hemoglobinHistory = hemoglobinPoints,
                    medals            = buildMedals(progress.currentStreak, progress.bestStreak)
                )
            } catch (_: Exception) {
                loadMockData()
            }
        }
    }

    private fun buildMedals(currentStreak: Int, bestStreak: Int): List<Medal> = listOf(
        Medal("first-week",        MedalType.FIRST_WEEK,          "First Week",         "Completa los 7 días sin fallar",           bestStreak >= 7,  minOf(bestStreak, 7),  7,  "¡7 días completados!"),
        Medal("first-month",       MedalType.FIRST_MONTH,         "First Month",        "Completa los 30 días sin fallar",          bestStreak >= 30, minOf(bestStreak, 30), 30, "¡30 días completados!"),
        Medal("half-treatment",    MedalType.HALF_TREATMENT,      "Half Treatment",     "Completa la mitad del tratamiento (45 d)", bestStreak >= 45, minOf(bestStreak, 45), 45, "¡Mitad del camino!"),
        Medal("treatment-completed",MedalType.TREATMENT_COMPLETED,"Treatment Completed","Completa el tratamiento (90 días)",        bestStreak >= 90, minOf(bestStreak, 90), 90, "¡Tratamiento completo!"),
        Medal("streak-recovered",  MedalType.STREAK_RECOVERED,    "Streak Recovered",   "Recupera tu racha perdida (7 días)",       currentStreak >= 7 && bestStreak > currentStreak, minOf(currentStreak, 7), 7, "¡Racha recuperada!")
    )

    private fun loadMockData() {
        _state.value = ProgressStats(
            healthStatus      = "Activo",
            totalPoints       = 70,
            currentStreak     = 7,
            longestStreak     = 30,
            currentHemoglobin = 11.2f,
            hemoglobinHistory = listOf(
                HemoglobinPoint("12 Abril",  7.0f),
                HemoglobinPoint("14 Mayo",   8.0f),
                HemoglobinPoint("12 Junio",  9.0f),
                HemoglobinPoint("28 Julio", 11.2f)
            ),
            medals = buildMedals(7, 30)
        )
    }

    fun getMedalById(id: String): Medal? =
        _state.value.medals.firstOrNull { it.id == id }
}
