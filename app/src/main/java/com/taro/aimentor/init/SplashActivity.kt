package com.taro.aimentor.init

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.onboarding.OnboardingActivity
import com.taro.aimentor.persistence.PreferencesManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }

        val preferencesManager = PreferencesManager.getInstance(this)
        if (preferencesManager.userId.isBlank()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
