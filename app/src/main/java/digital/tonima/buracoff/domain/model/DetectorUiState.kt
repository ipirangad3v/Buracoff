package digital.tonima.buracoff.domain.model

data class DetectorUiState(
    val currentGForce: Double = 0.0,
    val potholeCount: Int = 0,
    val lastImpactForce: Double = 0.0,
    val isMonitoring: Boolean = false
)

