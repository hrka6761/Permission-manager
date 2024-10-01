package ir.hrka.permission_manager

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PermissionManager @Inject constructor(
    @ActivityContext private val context: Context
) {

    private val activity: ComponentActivity
        @Throws(IllegalArgumentException::class)
        get() {
            if (context !is ComponentActivity)
                throw IllegalArgumentException("The passed context must be an ComponentActivity")

            return context
        }
    private val permissionCallback = PermissionCallback()
    private var launcher: ActivityResultLauncher<String> = initLauncher()


    fun getPermission(permission: String, callback: ((Boolean) -> Unit)? = null) {

        if (!hasRequiredPermission(permission)) {
            callback?.let { permissionCallback.callback = callback }
            launcher.launch(permission)
        } else {
            if (callback != null)
                callback(true)
            else
                Toast.makeText(activity, "Permission already granted", Toast.LENGTH_LONG).show()
        }
    }


    private fun initLauncher(): ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            permissionCallback
        )

    @Throws(IllegalArgumentException::class)
    private fun hasRequiredPermission(permission: String): Boolean {

        if (!permission.startsWith("android.permission."))
            throw IllegalArgumentException("The permission must be a member of the android.Manifest.permission class.")

        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private inner class PermissionCallback : ActivityResultCallback<Boolean> {

        var callback: (Boolean) -> Unit = { isGranted ->
            val msg = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }

        override fun onActivityResult(result: Boolean) {
            callback(result)
        }
    }
}