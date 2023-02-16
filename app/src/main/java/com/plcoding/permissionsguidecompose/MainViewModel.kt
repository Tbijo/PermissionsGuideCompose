package com.plcoding.permissionsguidecompose

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    // We want to show multiple dialogs (one after the other) because the user may decline all of the permissions
    // We need to queue this dialogs, queue data structure
    // String will be the permission
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    // for dismissing a dialog by clicking OK or outside the dialog
    // We want to pop the entry of our queue
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    // call this function when we get permission results
    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        // if the permission was not granted we want to put it into our queue on the first index
        // And the permission should not be duplicated in our queue
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    // Add permissions to Manifest
}