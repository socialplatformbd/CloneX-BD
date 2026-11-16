package com.clonexbd.app

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class CloneXAdminReceiver : DeviceAdminReceiver() {

    override fun onProfileProvisioningComplete(
        context: Context,
        intent: Intent
    ) {
        super.onProfileProvisioningComplete(context, intent)

        val policyManager = context.getSystemService(
            Context.DEVICE_POLICY_SERVICE
        ) as DevicePolicyManager

        val adminComponent = ComponentName(
            context,
            CloneXAdminReceiver::class.java
        )

        runCatching {
            policyManager.setProfileName(
                adminComponent,
                "CloneX BD"
            )

            policyManager.setProfileEnabled(adminComponent)
        }
    }
}
