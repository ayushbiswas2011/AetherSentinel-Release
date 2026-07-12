package com.aethersentinel.showcase

import android.app.Application
import com.aethersentinel.core.AetherConfig
import com.aethersentinel.core.OkAether

/**
 * Application subclass: OkAetherApp.
 * Deliberately named differently from the root composable OkAetherShowcaseRoot()
 * to prevent any class/function naming collision.
 */
class OkAetherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // One-line SDK bootstrap — showcased verbatim in the Developer
        // Sandbox tab's initialization snippet.
        OkAether.bootstrap(
            context = this,
            licenseKey = null,
            config = AetherConfig.DEFAULT
        )
    }
}
