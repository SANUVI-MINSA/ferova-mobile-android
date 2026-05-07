package pe.edu.upc.ferovafamily.presentation.achievements

object AchievementsRoutes {
    const val ACHIEVEMENTS = "achievements"
    const val ACHIEVEMENT_DETAIL = "achievement_detail/{achievementId}"

    fun detail(achievementId: String) = "achievement_detail/$achievementId"
}