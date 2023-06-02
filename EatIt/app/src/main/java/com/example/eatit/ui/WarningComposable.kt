package com.example.eatit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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