package pe.edu.upc.ferovafamily.data.mapper

import pe.edu.upc.ferovafamily.data.remote.dto.HemoglobinRecordDto
import pe.edu.upc.ferovafamily.data.remote.dto.PatientResponse
import pe.edu.upc.ferovafamily.domain.model.HemoglobinRecord
import pe.edu.upc.ferovafamily.domain.model.Patient

fun PatientResponse.toDomain(): Patient = Patient(
    id        = id,
    name      = name,
    lastName  = lastName,
    birthDate = birthDate  ?: "",
    gender    = gender     ?: "",
    weight    = weight     ?: 0.0,
    height    = height     ?: 0.0,
    motherId  = motherId
)

fun HemoglobinRecordDto.toDomain(): HemoglobinRecord = HemoglobinRecord(
    date  = date  ?: "",
    value = value ?: 0f,
    unit  = unit  ?: "g/dL"
)
