package digital.tonima.buracoff.presentation.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import digital.tonima.buracoff.presentation.service.PotholeService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = mutableListOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.VIBRATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= 34) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
        }
        permissionLauncher.launch(permissions.toTypedArray())

        setContent {
            PotholeScreen(
                onStartService = {
                    startForegroundService(Intent(this, PotholeService::class.java))
                },
                onStopService = {
                    stopService(Intent(this, PotholeService::class.java))
                }
            )
        }
    }
}

