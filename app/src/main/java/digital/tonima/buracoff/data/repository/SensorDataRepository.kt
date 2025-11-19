package digital.tonima.buracoff.data.repository

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SensorData(val x: Float, val y: Float, val z: Float)

@Singleton
class SensorDataRepository @Inject constructor(
    private val sensorManager: SensorManager
) {

    val sensorReadings: Flow<SensorData> = callbackFlow {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    trySend(SensorData(it.values[0], it.values[1], it.values[2]))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        } else {
            close(Exception("Acelerômetro Linear não disponível neste dispositivo"))
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

