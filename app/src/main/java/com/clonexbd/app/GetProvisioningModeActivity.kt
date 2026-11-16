package com.clonexbd.app

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Bundle

class GetProvisioningModeActivity : Activity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val allowedModes = intent.getIntegerArrayListExtra(
            DevicePolicyManager.EXTRA_PROVISIONING_ALLOWED_PROVISIONING_MODES
        )

        val managedProfileMode =
            DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE

        if (allowedModes != null && managedProfileMode in allowedModes) {
            val result = Intent().putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_MODE,
                managedProfileMode
            )
            setResult(RESULT_OK, result)
        } else {
            setResult(RESULT_CANCELED)
        }

        finish()
    }
}
