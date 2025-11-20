package digital.tonima.buracoff.domain.usecase

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import digital.tonima.buracoff.domain.model.SensorDataPoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class PotholeDetector @Inject constructor(
    private val sensorManager: SensorManager
) : SensorEventListener {
    private val _potholeEvents = MutableSharedFlow<Double>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val potholeEvents = _potholeEvents.asSharedFlow()

    private val _sensorData = MutableSharedFlow<SensorDataPoint>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sensorData = _sensorData.asSharedFlow()

    private val THRESHOLD_Z_ACCEL = 8.0
    private var isRunning = false

    fun start() {
        if (isRunning) return
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            isRunning = true
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        isRunning = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val magnitude = sqrt((x * x + y * y + z * z).toDouble())
            val isPothole = magnitude > THRESHOLD_Z_ACCEL

            val dataPoint = SensorDataPoint(
                timestamp = System.currentTimeMillis(),
                magnitude = magnitude,
                isPothole = isPothole
            )

            _sensorData.tryEmit(dataPoint)

            if (isPothole) {
                _potholeEvents.tryEmit(magnitude)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

