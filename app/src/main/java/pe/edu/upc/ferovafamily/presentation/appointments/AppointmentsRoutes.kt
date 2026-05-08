package pe.edu.upc.ferovafamily.presentation.appointments

object AppointmentsRoutes {
    const val HEALTH_CENTERS_MAP = "health_centers_map"
    const val HEALTH_CENTER_DETAIL = "health_center_detail/{centerId}"
    const val APPOINTMENT_BOOKING = "appointment_booking/{centerId}"
    const val TIME_SLOT_SELECTION = "time_slot_selection/{centerId}/{patientId}/{dateIso}"
    const val APPOINTMENT_CONFIRMED = "appointment_confirmed/{appointmentId}"

    fun healthCenterDetail(centerId: String) = "health_center_detail/$centerId"
    fun appointmentBooking(centerId: String) = "appointment_booking/$centerId"
    fun timeSlotSelection(centerId: String, patientId: String, dateIso: String) =
        "time_slot_selection/$centerId/$patientId/$dateIso"
    fun appointmentConfirmed(appointmentId: String) = "appointment_confirmed/$appointmentId"
}