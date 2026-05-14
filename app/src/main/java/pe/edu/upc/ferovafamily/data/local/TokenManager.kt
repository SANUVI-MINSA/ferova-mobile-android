package pe.edu.upc.ferovafamily.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Almacena el JWT y los datos básicos del usuario en SharedPreferences.
 * Úsalo como singleton vía TokenManager.getInstance(context).
 */
class TokenManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Token ──────────────────────────────────────────────────────────
    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    // ── User info ──────────────────────────────────────────────────────
    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var userName: String?
        get() = prefs.getString(KEY_USER_NAME, null)
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userLastName: String?
        get() = prefs.getString(KEY_USER_LASTNAME, null)
        set(value) = prefs.edit().putString(KEY_USER_LASTNAME, value).apply()

    var userRole: String?
        get() = prefs.getString(KEY_USER_ROLE, null)
        set(value) = prefs.edit().putString(KEY_USER_ROLE, value).apply()

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    val fullName: String
        get() = listOfNotNull(userName, userLastName).joinToString(" ").ifBlank { "Usuario" }

    // ── Recuperación de contraseña (estado temporal) ───────────────────────
    var recoveryEmail: String?
        get() = prefs.getString(KEY_RECOVERY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_RECOVERY_EMAIL, value).apply()

    var recoveryCode: String?
        get() = prefs.getString(KEY_RECOVERY_CODE, null)
        set(value) = prefs.edit().putString(KEY_RECOVERY_CODE, value).apply()

    val isLoggedIn: Boolean
        get() = token != null && userId != null

    fun clearRecovery() {
        prefs.edit().remove(KEY_RECOVERY_EMAIL).remove(KEY_RECOVERY_CODE).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "ferova_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_LASTNAME = "user_lastname"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_RECOVERY_EMAIL = "recovery_email"
        private const val KEY_RECOVERY_CODE = "recovery_code"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context).also { INSTANCE = it }
            }
    }
}
