package com.example.mobile_application_development

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.core.content.ContextCompat
import com.example.mobile_application_development.ui.theme.Mobile_Application_DevelopmentTheme

class ThirdActivity : ComponentActivity() {

    companion object {
        const val ACTION_CUSTOM_BROADCAST = "com.example.mobile_application_development.CUSTOM_BROADCAST"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }

    private var customReceiver: BroadcastReceiver? = null
    private var receivedMessageState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val broadcastType = intent.getStringExtra("EXTRA_BROADCAST_TYPE") ?: "CUSTOM"
        val customMessage = intent.getStringExtra("EXTRA_CUSTOM_MESSAGE") ?: ""

        if (broadcastType == "CUSTOM") {
            customReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == ACTION_CUSTOM_BROADCAST) {
                        val msg = intent.getStringExtra(EXTRA_MESSAGE)
                        receivedMessageState.value = msg
                    }
                }
            }

            val filter = IntentFilter(ACTION_CUSTOM_BROADCAST)
            ContextCompat.registerReceiver(
                this,
                customReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        setContent {
            Mobile_Application_DevelopmentTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ThirdScreenContent(
                        broadcastType = broadcastType,
                        customMessage = customMessage,
                        receivedBroadcastMessage = receivedMessageState.value,
                        modifier = Modifier.padding(innerPadding),
                        onTriggerBroadcast = { message ->
                            val intent = Intent(ACTION_CUSTOM_BROADCAST).apply {
                                putExtra(EXTRA_MESSAGE, message)
                                setPackage(packageName)
                            }
                            sendBroadcast(intent)
                        },
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }
    }
}

@Composable
fun ThirdScreenContent(
    broadcastType: String,
    customMessage: String,
    receivedBroadcastMessage: String?,
    modifier: Modifier = Modifier,
    onTriggerBroadcast: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (broadcastType == "CUSTOM") {
            Text(
                text = "Third Activity: Custom Broadcast Receiver",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Message passed from Second Activity:\n\"$customMessage\"",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onTriggerBroadcast(customMessage) }
            ) {
                Text("Send / Trigger Custom Broadcast")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (receivedBroadcastMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📢 Broadcast Receiver Result:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = receivedBroadcastMessage,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Third Activity: System Battery Selected",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ℹ️ Nothing to do here.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "As specified: Option 2 (System battery notification) was selected in the first activity.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) {
            Text("Back to Home")
        }
    }
}