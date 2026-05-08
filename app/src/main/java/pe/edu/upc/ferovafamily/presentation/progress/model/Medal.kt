package pe.edu.upc.ferovafamily.presentation.progress.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

data class Medal(
    val id: String,
    val type: MedalType,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val currentDays: Int,
    val targetDays: Int,
    val celebrationMessage: String
) {
    val progress: Float
        get() = (currentDays.toFloat() / targetDays).coerceIn(0f, 1f)
}

enum class MedalType(
    val displayName: String,
    val celebrationLabel: String,
    val icon: ImageVector
) {
    FIRST_WEEK("First Week", "FIRST WEEK", Icons.Default.WorkspacePremium),
    FIRST_MONTH("First Month", "FIRST MONTH", Icons.Default.Favorite),
    HALF_TREATMENT("Half Treatment", "HALF TREATMENT", Icons.Default.Star),
    TREATMENT_COMPLETED("Treatment Completed", "TREATMENT COMPLETED", Icons.Default.MilitaryTech),
    STREAK_RECOVERED("Streak Recovered", "STREAK RECOVERED", Icons.Default.Bolt);

    companion object {
        val defaultIcon: ImageVector = Icons.Default.EmojiEvents
    }
}