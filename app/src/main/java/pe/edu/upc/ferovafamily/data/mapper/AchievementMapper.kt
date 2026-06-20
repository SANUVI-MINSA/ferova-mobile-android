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
    healthStatus  = status        ?: ""
)

fun BadgeDto.toDomain(currentStreak: Int = 0): Badge {
    val targetDays = milestone ?: daysNeeded ?: 1

    val completedDays = when {
        isUnlocked == true -> targetDays
        progress != null && progress > 0 -> (progress * targetDays / 100).coerceIn(0, targetDays)
        else -> min(currentStreak, targetDays)
    }

    return Badge(
        id              = id ?: "",
        name            = name ?: "",
        description     = description ?: "",
        isUnlocked      = isUnlocked ?: false,
        currentProgress = completedDays,
        targetProgress  = targetDays,
        unlockedAt      = unlockedAt,
        category        = type ?: ""
    )
}