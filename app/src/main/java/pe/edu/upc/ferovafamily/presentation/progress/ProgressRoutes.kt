package pe.edu.upc.ferovafamily.presentation.progress

object ProgressRoutes {
    const val PROGRESS = "progress"
    const val DOSE_CONFIRMED = "dose_confirmed"
    const val MEDAL_UNLOCKED = "medal_unlocked/{medalId}"
    const val STREAK_LOST = "streak_lost"

    fun medalUnlocked(medalId: String) = "medal_unlocked/$medalId"
}