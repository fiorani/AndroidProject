package com.example.eatit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.viewModel.WarningViewModel

@Composable
internal fun GPSAlertDialogComposable(
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    AlertDialog(
        onDismissRequest = {
            warningViewModel.setGPSAlertDialogVisibility(false)
        },
        title = {
            Text(text = "GPS disabled")
        },
        text = {
            Text(text = "GPS is turned off but is needed to get the coordinates")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if (intent.resolveActivity(applicationContext.packageManager) != null) {
                        applicationContext.startActivity(intent)
                    }
                    warningViewModel.setGPSAlertDialogVisibility(false)
                }
            ) {
                Text("Turned on the GPS")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { warningViewModel.setGPSAlertDialogVisibility(false) }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
internal fun PermissionSnackBarComposable(
    snackbarHostState: SnackbarHostState,
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = "Permission are needed to get your position",
            actionLabel = "Go to settings"
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", applicationContext.packageName, null)
                }
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    applicationContext.startActivity(intent)
                }
            }

            SnackbarResult.Dismissed -> {
                warningViewModel.setPermissionSnackBarVisibility(false)
            }
        }
    }
}

@Composable
fun ConnectivitySnackBarComposable(
    snackbarHostState: SnackbarHostState,
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = "No Internet available",
            actionLabel = "Go to settings",
            duration = SnackbarDuration.Indefinite
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    applicationContext.startActivity(intent)
                }
            }

            SnackbarResult.Dismissed -> {
                warningViewModel.setConnectivitySnackBarVisibility(false)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelDialog (onDismissRequest: () -> Unit, text: String, cancellingQuery: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(30.dp))
            Text(
                modifier = Modifier.fillMaxWidth().padding(10.dp, 5.dp),
                text = "Attention!",
                fontSize = 25.sp,
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp),
                text = text,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    modifier = Modifier.padding(0.dp, 10.dp),
                    onClick = {
                        cancellingQuery()
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    modifier = Modifier.padding(10.dp, 10.dp),
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "Back",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
    }
}