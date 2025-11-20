package digital.tonima.buracoff.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import digital.tonima.buracoff.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SensorData(val x: Float, val y: Float, val z: Float)

@Singleton
class SensorDataRepository @Inject constructor(
    private val sensorManager: SensorManager,
    @ApplicationContext private val context: Context
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
            close(Exception(context.getString(R.string.error_sensor_not_available)))
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

