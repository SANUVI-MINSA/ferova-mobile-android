package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.AchievementProgressDto
import pe.edu.upc.ferovafamily.data.remote.dto.BadgeDto
import pe.edu.upc.ferovafamily.domain.model.AchievementProgress
import pe.edu.upc.ferovafamily.domain.model.Badge

// Backend usa: totalPoints, currentStreak, longestStreak
fun AchievementProgressDto.toDomain(): AchievementProgress = AchievementProgress(
    points        = totalPoints   ?: 0,
    currentStreak = currentStreak ?: 0,
    bestStreak    = longestStreak ?: 0,
    healthStatus  = status        ?: "Activo"
)

fun BadgeDto.toDomain(): Badge = Badge(
    id              = id              ?: "",
    name            = name            ?: "",
    description     = description     ?: "",
    isUnlocked      = isUnlocked      ?: false,
    currentProgress = currentProgress ?: 0,
    targetProgress  = targetProgress  ?: 1,
    unlockedAt      = unlockedAt,
    category        = category        ?: ""
)
