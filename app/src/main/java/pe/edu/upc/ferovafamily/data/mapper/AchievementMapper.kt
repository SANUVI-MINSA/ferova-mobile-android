package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.AchievementProgressDto
import pe.edu.upc.ferovafamily.data.remote.dto.BadgeDto
import pe.edu.upc.ferovafamily.domain.model.AchievementProgress
import pe.edu.upc.ferovafamily.domain.model.Badge
import kotlin.math.min

fun AchievementProgressDto.toDomain(): AchievementProgress = AchievementProgress(
    points        = totalPoints   ?: 0,
    currentStreak = currentStreak ?: 0,
    bestStreak    = longestStreak ?: 0,
    healthStatus  = status        ?: "Activo"
)

fun BadgeDto.toDomain(currentStreak: Int = 0): Badge = Badge(
    id              = id ?: "",
    name            = name ?: "",
    description     = description ?: "",
    isUnlocked      = isUnlocked ?: false,
    currentProgress = min(currentStreak, milestone ?: daysNeeded ?: 1),
    targetProgress  = milestone ?: daysNeeded ?: 1,
    unlockedAt      = unlockedAt,
    category        = type ?: ""
)