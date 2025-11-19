package digital.tonima.buracoff.presentation.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import digital.tonima.buracoff.R
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

@Composable
fun PotholeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val lastImpact by viewModel.lastImpact.collectAsState()
    val count by viewModel.potholeCount.collectAsState()
    var isServiceRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.title_pothole_detector),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = if (lastImpact > 0) Color(0xFFCF6679) else Color(0xFF03DAC5),
                    shape = CircleShape
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.label_impact),
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(R.string.format_impact_value, lastImpact),
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.label_total_detected, count),
            color = Color.Gray,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    isServiceRunning = true
                    onStartService()
                },
                enabled = !isServiceRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
            ) {
                Text(stringResource(R.string.button_start_monitoring))
            }

            Button(
                onClick = {
                    isServiceRunning = false
                    onStopService()
                },
                enabled = isServiceRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3700B3))
            ) {
                Text(stringResource(R.string.button_stop))
            }
        }
    }
}

