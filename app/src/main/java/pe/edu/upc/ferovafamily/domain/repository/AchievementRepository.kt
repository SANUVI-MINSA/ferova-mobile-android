package pe.edu.upc.ferovafamily.domain.repository

import pe.edu.upc.ferovafamily.domain.model.AchievementProgress
import pe.edu.upc.ferovafamily.domain.model.Badge

interface AchievementRepository {
    suspend fun getAchievementProgress(patientId: String): AchievementProgress
    suspend fun getBadges(patientId: String): List<Badge>
}
