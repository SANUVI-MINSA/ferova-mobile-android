package pe.edu.upc.ferovafamily.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.JsonObject
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.UserApiService
import pe.edu.upc.ferovafamily.data.remote.dto.LoginRequest
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterMotherRequest
import pe.edu.upc.ferovafamily.data.remote.dto.RequestPasswordCodeRequest
import pe.edu.upc.ferovafamily.data.remote.dto.ResetPasswordRequest
import pe.edu.upc.ferovafamily.data.remote.dto.VerifyPasswordCodeRequest

// ── UI State ──────────────────────────────────────────────────────────────────

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

data class AuthUiState(
    val loginResult: AuthResult = AuthResult.Idle,
    val registerResult: AuthResult = AuthResult.Idle,
    val requestCodeResult: AuthResult = AuthResult.Idle,
    val verifyCodeResult: AuthResult = AuthResult.Idle,
    val resetPasswordResult: AuthResult = AuthResult.Idle
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val userService = FerovaApiClient.create(UserApiService::class.java, application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

// Reemplaza la función login completa (líneas 52-125) con esto:

    fun login(dni: String, password: String) {
        if (dni.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginResult = AuthResult.Error("Completa todos los campos")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loginResult = AuthResult.Loading) }

            try {
                // 1. Login
                val response = userService.login(LoginRequest(dni.trim(), password))

                if (!response.isSuccessful) {
                    val msg = parseErrorBody(response.errorBody()) ?: when (response.code()) {
                        401 -> "DNI o contraseña incorrectos"
                        404 -> "Usuario no encontrado"
                        else -> "Error al iniciar sesión (${response.code()})"
                    }
                    _uiState.update { it.copy(loginResult = AuthResult.Error(msg)) }
                    return@launch
                }

                val token = response.body()!!.token
                tokenManager.token = token

                // 2. Decodificar JWT (esto es rápido, está bien en main thread)
                val userId = decodeJwtField(token, "id") ?: decodeJwtField(token, "motherId")

                if (userId == null) {
                    _uiState.update {
                        it.copy(loginResult = AuthResult.Error("Token inválido: no se encontró userId"))
                    }
                    return@launch
                }

                tokenManager.userId = userId

                // 3. Obtener usuario (esto ya está en coroutine)
                val userResp = userService.getUserById(userId)

                if (!userResp.isSuccessful) {
                    // Si falla pero es madre (modo compatibilidad)
                    _uiState.update { it.copy(loginResult = AuthResult.Success) }
                    return@launch
                }

                val user = userResp.body()!!
                tokenManager.userName = user.name
                tokenManager.userLastName = user.lastname
                tokenManager.userRole = user.role
                tokenManager.userEmail = user.email

                // 4. Validar rol - CORREGIDO
                when (user.role) {
                    "Mother" -> {
                        _uiState.update { it.copy(loginResult = AuthResult.Success) }
                    }
                    "Admin", "Nurse" -> {
                        tokenManager.clear()
                        _uiState.update {
                            it.copy(loginResult = AuthResult.Error(
                                "ACCESO DENEGADO\n\nFerovaFamily es exclusiva para madres.\n\nTu cuenta es de tipo: ${user.role}"
                            ))
                        }
                    }
                    else -> {
                        tokenManager.clear()
                        _uiState.update {
                            it.copy(loginResult = AuthResult.Error(
                                "Rol de usuario no válido para esta aplicación: ${user.role}"
                            ))
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loginResult = AuthResult.Error(
                        "Error de conexión: ${e.message ?: "Verifica tu internet"}"
                    ))
                }
            }
        }
    }

    /** Intenta extraer el mensaje de error del cuerpo JSON del backend */
    private fun parseErrorBody(errorBody: okhttp3.ResponseBody?): String? {
        return try {
            val json = Gson().fromJson(errorBody?.string(), JsonObject::class.java)
            json?.get("message")?.asString
                ?: json?.get("error")?.asString
                ?: json?.get("msg")?.asString
        } catch (_: Exception) { null }
    }

    fun resetLoginResult() {
        _uiState.update { it.copy(loginResult = AuthResult.Idle) }
    }

    /**
     * Decodifica el payload (parte central) del JWT y extrae un campo string.
     * No valida la firma — solo se usa para leer datos propios ya autenticados.
     */
    private fun decodeJwtField(token: String, field: String): String? {
        return try {
            val payloadB64 = token.split(".").getOrNull(1) ?: return null
            val decoded = String(
                android.util.Base64.decode(payloadB64, android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING),
                Charsets.UTF_8
            )
            Gson().fromJson(decoded, JsonObject::class.java)?.get(field)?.asString
        } catch (_: Exception) { null }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun registerMother(
        name: String,
        lastname: String,
        dni: String,
        email: String,
        phone: String,
        password: String
    ) {
        if (listOf(name, lastname, dni, email, password).any { it.isBlank() }) {
            _uiState.update { it.copy(registerResult = AuthResult.Error("Completa todos los campos")) }
            return
        }
        // Formatear teléfono al formato que exige el backend: "+51 XXXXXXXXX"
        val cleanPhone = phone.trim().replace(" ", "").let { p ->
            when {
                p.startsWith("+51") && p.length > 3 -> "+51 ${p.substring(3)}"
                p.startsWith("51") && p.length > 2  -> "+51 ${p.substring(2)}"
                p.length == 9                        -> "+51 $p"
                else                                 -> p
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(registerResult = AuthResult.Loading) }
            try {
                val response = userService.registerMother(
                    RegisterMotherRequest(
                        name = name.trim(),
                        lastname = lastname.trim(),
                        dni = dni.trim(),
                        email = email.trim().lowercase(),
                        phone = cleanPhone,
                        password = password
                    )
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(registerResult = AuthResult.Success) }
                } else {
                    // Intentar leer el mensaje real del backend
                    val backendMsg = parseErrorBody(response.errorBody())
                    val msg = backendMsg ?: when (response.code()) {
                        409 -> "El DNI o correo ya está registrado"
                        400 -> "Datos inválidos (${response.code()}). Verifica DNI, correo y contraseña"
                        else -> "Error al registrarse (${response.code()})"
                    }
                    _uiState.update { it.copy(registerResult = AuthResult.Error(msg)) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(registerResult = AuthResult.Error("Sin conexión. Revisa tu internet."))
                }
            }
        }
    }

    fun resetRegisterResult() {
        _uiState.update { it.copy(registerResult = AuthResult.Idle) }
    }

    // ── Password Recovery ─────────────────────────────────────────────────────

    fun requestPasswordCode(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(requestCodeResult = AuthResult.Error("Ingresa tu correo")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(requestCodeResult = AuthResult.Loading) }
            try {
                android.util.Log.d("RECOVERY_DEBUG", "1. Email enviado: '${email.trim()}'")

                // 1. Verificar si el usuario existe y obtener su rol
                val userResponse = userService.getUserByEmail(email.trim())


                if (!userResponse.isSuccessful || userResponse.body() == null) {
                    _uiState.update {
                        it.copy(requestCodeResult = AuthResult.Error("Correo no registrado en el sistema"))
                    }
                    return@launch
                }

                val user = userResponse.body()!!
                android.util.Log.d("RECOVERY_DEBUG", "5. Usuario: ${user.email}, Rol: ${user.role}")


                // 2. Validar que sea MADRE
                if (user.role != "Mother") {
                    _uiState.update {
                        it.copy(requestCodeResult = AuthResult.Error(
                                    "ACCESO DENEGADO\n\n" +
                                    "Este correo pertenece a: ${user.role}\n" +
                                    "La recuperación de contraseña es solo para madres.\n\n"
                        ))
                    }
                    return@launch
                }

                android.util.Log.d("RECOVERY_DEBUG", "6. Enviando código para: ${email.trim()}")
                // 3. Es madre, enviar código de recuperación
                val response = userService.requestPasswordCode(RequestPasswordCodeRequest(email.trim()))

                android.util.Log.d("RECOVERY_DEBUG", "7. Código respuesta: ${response.code()}")
                android.util.Log.d("RECOVERY_DEBUG", "8. isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    tokenManager.recoveryEmail = email.trim()
                    _uiState.update { it.copy(requestCodeResult = AuthResult.Success) }
                } else {
                    _uiState.update {
                        it.copy(requestCodeResult = AuthResult.Error("Error al enviar el código. Intenta nuevamente."))
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RECOVERY_DEBUG", "Excepción: ${e.message}", e)
                _uiState.update {
                    it.copy(requestCodeResult = AuthResult.Error("Error: ${e.message}"))
                }
            }
        }
    }

    fun verifyPasswordCode(email: String, code: String) {
        if (email.isBlank() || code.isBlank()) {
            _uiState.update { it.copy(verifyCodeResult = AuthResult.Error("Completa todos los campos")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(verifyCodeResult = AuthResult.Loading) }
            try {
                val response = userService.verifyPasswordCode(VerifyPasswordCodeRequest(email.trim(), code.trim()))
                if (response.isSuccessful) {
                    _uiState.update { it.copy(verifyCodeResult = AuthResult.Success) }
                } else {
                    _uiState.update {
                        it.copy(verifyCodeResult = AuthResult.Error("Código inválido o expirado"))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(verifyCodeResult = AuthResult.Error("Sin conexión. Revisa tu internet."))
                }
            }
        }
    }

    fun resetPassword(email: String, code: String, newPassword: String) {
        if (listOf(email, code, newPassword).any { it.isBlank() }) {
            _uiState.update { it.copy(resetPasswordResult = AuthResult.Error("Completa todos los campos")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(resetPasswordResult = AuthResult.Loading) }
            try {
                val response = userService.resetPassword(
                    ResetPasswordRequest(email.trim(), code.trim(), newPassword)
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(resetPasswordResult = AuthResult.Success) }
                } else {
                    _uiState.update {
                        it.copy(resetPasswordResult = AuthResult.Error("No se pudo restablecer la contraseña"))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(resetPasswordResult = AuthResult.Error("Sin conexión. Revisa tu internet."))
                }
            }
        }
    }
}
