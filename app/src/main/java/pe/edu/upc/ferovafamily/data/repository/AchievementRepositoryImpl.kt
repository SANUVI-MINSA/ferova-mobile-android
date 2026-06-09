package pe.edu.upc.ferovafamily.data.repository

import android.util.Log
import pe.edu.upc.ferovafamily.data.mapper.toDomain
import pe.edu.upc.ferovafamily.data.remote.api.AchievementApiService
import pe.edu.upc.ferovafamily.domain.model.AchievementProgress
import pe.edu.upc.ferovafamily.domain.model.Badge
import pe.edu.upc.ferovafamily.domain.repository.AchievementRepository

private const val TAG = "AchievementRepo"

class AchievementRepositoryImpl(
    private val service: AchievementApiService
) : AchievementRepository {

    override suspend fun getAchievementProgress(patientId: String): AchievementProgress {
        Log.d(TAG, "getAchievementProgress called with patientId: $patientId")
        return try {
            val response = service.getAchievementProgress(patientId)
            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")

                body?.let { dto ->
                    Log.d(TAG, "totalPoints: ${dto.totalPoints}, currentStreak: ${dto.currentStreak}, longestStreak: ${dto.longestStreak}, status: ${dto.status}")
                    AchievementProgress(
                        points = dto.totalPoints ?: 0,
                        currentStreak = dto.currentStreak ?: 0,
                        bestStreak = dto.longestStreak ?: 0,
                        healthStatus = dto.status ?: "",
                    )
                } ?: run {
                    Log.e(TAG, "Response body is null")
                    mockProgress()
                }
            } else {
                Log.e(TAG, "Response not successful: ${response.code()} - ${response.message()}")
                mockProgress()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getAchievementProgress", e)
            mockProgress()
        }
    }

    override suspend fun getBadges(patientId: String): List<Badge> {
        Log.d(TAG, "getBadges called with patientId: $patientId")
        return try {
            val progress = getAchievementProgress(patientId)
            val currentStreak = progress.currentStreak
            Log.d(TAG, "Current streak for validation: $currentStreak")

            val response = service.getBadges(patientId)
            Log.d(TAG, "Badges response code: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Badges response body: $body")

                val badgesList = body?.badges
                Log.d(TAG, "Badges list size: ${badgesList?.size ?: 0}")

                badgesList?.forEachIndexed { index, badge ->
                    Log.d(TAG, "Badge $index: id=${badge.id}, type=${badge.type}, name=${badge.name}, isUnlocked=${badge.isUnlocked}, progress=${badge.progress}, milestone=${badge.milestone}")
                }

                val mapped = badgesList?.map { it.toDomain(currentStreak) } ?: emptyList()
                Log.d(TAG, "Mapped badges count: ${mapped.size}")
                mapped
            } else {
                Log.e(TAG, "Badges response not successful: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getBadges", e)
            emptyList()
        }
    }

    private fun mockProgress() = AchievementProgress(
        points = 0, currentStreak = 0, bestStreak = 0, healthStatus = "Pendiente"
    )
}