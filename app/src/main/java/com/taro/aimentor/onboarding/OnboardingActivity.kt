package com.taro.aimentor.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.databinding.OnboardingPageBinding

class OnboardingActivity: AppCompatActivity() {

    companion object {

        const val NUM_QUESTIONS = 3f
    }

    private lateinit var binding: OnboardingPageBinding
    private lateinit var fragmentController: OnboardingFragmentController
    private var questionNumber = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentController = OnboardingFragmentController(
            fragmentManager = supportFragmentManager,
            containerId = R.id.container
        )
        fragmentController.onStateChange(newState = OnboardingAskState.OCCUPATION)
        setProgress()
    }

    private fun setProgress() {
        val progress = (questionNumber / NUM_QUESTIONS) * 100f
        binding.progressBar.setProgressCompat(progress.toInt(), true)
    }
}
