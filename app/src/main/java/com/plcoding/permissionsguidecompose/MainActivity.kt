package com.plcoding.permissionsguidecompose

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.permissionsguidecompose.ui.theme.PermissionsGuideComposeTheme

class MainActivity : ComponentActivity() {

    // create this for the right order of permissions according to the queue
    // We want to show RECORD_AUDIO before CALL_PHONE perm.
    private val permissionsToRequest = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionsGuideComposeTheme {
                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                // one permission request
                val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                    // contract - which activity gets launched for what kind of result
                    // ActivityResultContracts - is a list of all contracts
                    contract = ActivityResultContracts.RequestPermission(),
                    // onResult - gets called when the user selects grants or declines a permission
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.CAMERA,
                            isGranted = isGranted
                        )
                    }
                    // This is just declaring a launcher we will launch it from a button
                )
                // Never put on one screen two request launchers because you will get results from both
                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            viewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        cameraPermissionResultLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }) {
                        Text(text = "Request one permission")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    }) {
                        Text(text = "Request multiple permission")
                    }
                }

                dialogQueue
                    // We want the first dialog to be the last
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.CAMERA -> {
                                    CameraPermissionTextProvider()
                                }
                                Manifest.permission.RECORD_AUDIO -> {
                                    RecordAudioPermissionTextProvider()
                                }
                                Manifest.permission.CALL_PHONE -> {
                                    PhoneCallPermissionTextProvider()
                                }
                                // a permission with did not want
                                else -> return@forEach
                            },
                            // Android does not support a way to find out whether a permission is permanently declined
                            // If should not show Request Permission Rationale for a given permission we can be sure that the permission was permanently declined
                            // it is not reliable because it will return false when we never requested the permission
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                            },
                            // Double decline go to settings
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        // package name of our application to show the detail settings
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}