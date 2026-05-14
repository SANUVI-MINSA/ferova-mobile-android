package pe.edu.upc.ferovafamily.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ─────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("dni") val dni: String,
    @SerializedName("password") val password: String
)

data class RegisterMotherRequest(
    @SerializedName("name") val name: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("dni") val dni: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class RequestPasswordCodeRequest(
    @SerializedName("email") val email: String
)

data class VerifyPasswordCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String,
    @SerializedName("newPassword") val newPassword: String
)

// ── Responses ─────────────────────────────────────────────────────────────────

// El backend de login devuelve únicamente el token.
// Los datos del usuario se obtienen después con GET /api/users/{id}
// (el id viene decodificando el payload del JWT).
data class LoginResponse(
    @SerializedName("token") val token: String
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("dni") val dni: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("role") val role: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)
