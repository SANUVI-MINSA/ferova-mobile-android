package pe.edu.upc.ferovafamily.presentation.achievements

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
import pe.edu.upc.ferovafamily.data.repository.AchievementRepositoryImpl
import pe.edu.upc.ferovafamily.domain.model.Badge
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository
import pe.edu.upc.ferovafamily.presentation.achievements.model.Achievement
import pe.edu.upc.ferovafamily.presentation.achievements.model.AchievementCategory

// UiState igual al original para no romper las screens existentes
data class AchievementsUiState(
    val totalPoints: Int = 0,
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false
)

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AchievementRepository = AchievementRepositoryImpl(
        FerovaApiClient.create(AchievementApiService::class.java, application)
    )
    private val tokenManager = TokenManager.getInstance(application)

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        val patientId = tokenManager.userId ?: "default"
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val progress     = repository.getAchievementProgress(patientId)
                val badges       = repository.getBadges(patientId)
                val achievements = badges.map { it.toPresentation() }

                _uiState.value = AchievementsUiState(
                    totalPoints   = progress.points,
                    unlockedCount = achievements.count { it.isUnlocked },
                    totalCount    = achievements.size,
                    achievements  = achievements,
                    isLoading     = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun getAchievementById(id: String): Achievement? =
        _uiState.value.achievements.firstOrNull { it.id == id }

    // ── Convierte Badge (domain) → Achievement (presentation) ────────────────
    private fun Badge.toPresentation() = Achievement(
        id              = id,
        title           = name,
        description     = description,
        longDescription = "Progreso: $currentProgress / $targetProgress",
        points          = targetProgress,   // el targetProgress hace de "puntos meta"
        isUnlocked      = isUnlocked,
        dateObtained    = unlockedAt,
        category        = when (category.uppercase()) {
            "STREAK"       -> AchievementCategory.STREAK
            "NUTRITION"    -> AchievementCategory.NUTRITION
            "CONSULTATION" -> AchievementCategory.CONSULTATIONS
            else           -> AchievementCategory.DOSE
        }
    )
}
