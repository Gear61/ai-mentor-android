package com.taro.aimentor.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.databinding.OnboardingPageBinding

class OnboardingActivity: AppCompatActivity() {

    private lateinit var binding: OnboardingPageBinding
    private lateinit var fragmentController: OnboardingFragmentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentController = OnboardingFragmentController(
            fragmentManager = supportFragmentManager,
            containerId = R.id.container
        )
        fragmentController.onStateChange(newState = OnboardingAskState.OCCUPATION)
    }
}
