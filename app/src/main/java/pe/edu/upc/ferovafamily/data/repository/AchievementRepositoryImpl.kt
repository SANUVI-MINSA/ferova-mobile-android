package pe.edu.upc.ferovafamily.data.repository

import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.AchievementApiService
import pe.edu.upc.ferovafamily.domain.model.AchievementProgress
import pe.edu.upc.ferovafamily.domain.model.Badge
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository

class AchievementRepositoryImpl(
    private val service: AchievementApiService
) : AchievementRepository {

    override suspend fun getAchievementProgress(patientId: String): AchievementProgress {
        return try {
            val response = service.getAchievementProgress(patientId)
            if (response.isSuccessful) response.body()?.toDomain() ?: mockProgress()
            else mockProgress()
        } catch (_: Exception) { mockProgress() }
    }

    override suspend fun getBadges(patientId: String): List<Badge> {
        return try {
            val response = service.getBadges(patientId)
            if (response.isSuccessful) {
                val body = response.body()
                // Si el backend devuelve lista vacía (sin tratamiento activo) → mock
                val badges = body?.badges?.map { it.toDomain() } ?: emptyList()
                if (badges.isEmpty()) mockBadges() else badges
            } else mockBadges()
        } catch (_: Exception) { mockBadges() }
    }

    // ── Mock data ─────────────────────────────────────────────────────────────

    private fun mockProgress() = AchievementProgress(
        points = 70, currentStreak = 7, bestStreak = 30, healthStatus = "Activo"
    )

    private fun mockBadges() = listOf(
        Badge("b-1", "Primera Dosis",    "Completaste tu primera dosis",       true,  1, 1,  category = "DOSE"),
        Badge("b-2", "Racha de 5 días",  "5 días consecutivos con la dosis",   true,  5, 5,  category = "STREAK"),
        Badge("b-3", "Primera Consulta", "Iniciaste tu primera consulta",       true,  1, 1,  category = "CONSULTATION"),
        Badge("b-4", "Racha de 15 días", "15 días consecutivos sin fallar",     false, 7, 15, category = "STREAK"),
        Badge("b-5", "Nutricionista",    "Registra 30 comidas en el diario",    false, 5, 30, category = "NUTRITION"),
        Badge("b-6", "Maestro del Hierro","Alcanza 100mg de hierro en un mes",  false, 0, 100,category = "NUTRITION")
    )
}
