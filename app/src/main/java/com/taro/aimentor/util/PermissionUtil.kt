package com.taro.aimentor.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {

    fun isPermissionGranted(permission: String, context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }
}
