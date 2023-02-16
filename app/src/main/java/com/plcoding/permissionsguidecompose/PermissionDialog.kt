package com.plcoding.permissionsguidecompose

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Rationale Dialog

@Composable
fun PermissionDialog(
    // Which text to show in the dialog according to permission
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    // listener if user clicks outside the dialog
    onDismiss: () -> Unit,
    // listener if user clicks on OK to request a permission again after it was declined once
    onOkClick: () -> Unit,
    // listener if the user needs to navigate to the settings if he declined a permission twice
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Divider()

                Text(
                    text = if(isPermanentlyDeclined) {
                        // Declined twice go to settings
                        "Grant permission"
                    } else {
                        // When want to grant the permission again
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        modifier = modifier
    )
}

// More scalable way to provide messages about the state of a permission to a user
interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you permanently declined camera permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your camera so that your friends " +
                    "can see you in a call."
        }
    }
}

class RecordAudioPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you permanently declined microphone permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your microphone so that your friends " +
                    "can hear you in a call."
        }
    }
}

class PhoneCallPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you permanently declined phone calling permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs phone calling permission so that you can talk " +
                    "to your friends."
        }
    }
}