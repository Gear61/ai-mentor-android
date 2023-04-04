package com.taro.aimentor.init

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.IoniconsModule

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Iconify.with(IoniconsModule())
    }
}
