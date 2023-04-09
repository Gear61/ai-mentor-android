package com.taro.aimentor.init

import android.app.Application
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.theme.ThemeManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(PreferencesManager.getInstance(this).themeMode)
    }
}
