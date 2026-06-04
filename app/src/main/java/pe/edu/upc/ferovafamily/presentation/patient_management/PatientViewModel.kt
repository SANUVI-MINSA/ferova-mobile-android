package pe.edu.upc.ferovafamily.presentation.patient_management

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.data.remote.FerovaApiClient
import pe.edu.upc.ferovafamily.data.remote.api.PatientApiService
import pe.edu.upc.ferovafamily.data.remote.dto.RegisterPatientRequest

sealed class PatientResult {
    object Idle : PatientResult()
    object Loading : PatientResult()
    object Success : PatientResult()
    data class Error(val message: String) : PatientResult()
}

data class PatientUiState(
    val registerResult: PatientResult = PatientResult.Idle
)

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val service = FerovaApiClient.create(PatientApiService::class.java, application)

    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> = _uiState.asStateFlow()

    fun registerPatient(
        name: String,
        lastName: String,
        birthDate: String,    // "2023-05-10"
        gender: String,       // "MALE" | "FEMALE"
        weight: Double,
        height: Double
    ) {
        if (listOf(name, lastName, birthDate, gender).any { it.isBlank() }) {
            _uiState.update { it.copy(registerResult = PatientResult.Error("Completa todos los campos")) }
            return
        }
        val motherId = tokenManager.userId
        if (motherId == null) {
            _uiState.update { it.copy(registerResult = PatientResult.Error("Sesión expirada. Vuelve a iniciar sesión.")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(registerResult = PatientResult.Loading) }
            try {
                val response = service.registerPatient(
                    RegisterPatientRequest(
                        name = name.trim(),
                        lastName = lastName.trim(),
                        birthDate = birthDate,
                        gender = gender,
                        weight = weight,
                        height = height,
                        motherId = motherId
                    )
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(registerResult = PatientResult.Success) }
                } else {
                    // Intentar leer el mensaje real del backend
                    val backendMsg = try {
                        val json = com.google.gson.Gson().fromJson(
                            response.errorBody()?.string(),
                            com.google.gson.JsonObject::class.java
                        )
                        json?.get("message")?.asString
                            ?: json?.get("error")?.asString
                    } catch (_: Exception) { null }

                    val msg = backendMsg ?: when (response.code()) {
                        409 -> "El paciente ya existe"
                        400 -> "Datos inválidos (${response.code()})"
                        else -> "Error al registrar (${response.code()})"
                    }
                    _uiState.update { it.copy(registerResult = PatientResult.Error(msg)) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(registerResult = PatientResult.Error("Sin conexión. Revisa tu internet."))
                }
            }
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(registerResult = PatientResult.Idle) }
    }
}
