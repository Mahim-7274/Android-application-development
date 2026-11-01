package com.example.mobile_application_development

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_application_development.ui.theme.Mobile_Application_DevelopmentTheme

class SecondActivity : ComponentActivity() {

    private var batteryReceiver: BroadcastReceiver? = null
    private var batteryLevelState = mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val broadcastType = intent.getStringExtra("EXTRA_BROADCAST_TYPE") ?: "CUSTOM"

        if (broadcastType == "BATTERY") {
            batteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        if (level >= 0 && scale > 0) {
                            batteryLevelState.value = (level * 100) / scale
                        }
                    }
                }
            }
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryReceiver, filter)
        }

        setContent {
            Mobile_Application_DevelopmentTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SecondScreenContent(
                        broadcastType = broadcastType,
                        batteryLevel = batteryLevelState.value,
                        modifier = Modifier.padding(innerPadding),
                        onProceedToThird = { customMessage ->
                            val thirdIntent = Intent(this, ThirdActivity::class.java).apply {
                                putExtra("EXTRA_BROADCAST_TYPE", broadcastType)
                                putExtra("EXTRA_CUSTOM_MESSAGE", customMessage)
                            }
                            startActivity(thirdIntent)
                        },
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                // Ignore if not registered
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreenContent(
    broadcastType: String,
    batteryLevel: Int?,
    modifier: Modifier = Modifier,
    onProceedToThird: (String) -> Unit,
    onBack: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (broadcastType == "CUSTOM") {
            Text(
                text = "Option 1: Custom Broadcast",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enter plain text message to pass to Third Activity:",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Plain Text Message") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onProceedToThird(userInput) },
                enabled = userInput.isNotBlank()
            ) {
                Text("Proceed to Third Activity")
            }
        } else {
            Text(
                text = "Option 2: System Battery Notification",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔋 Battery Percentage Receiver",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (batteryLevel != null) "$batteryLevel%" else "Fetching battery level...",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onProceedToThird("") }
            ) {
                Text("Proceed to Third Activity")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}