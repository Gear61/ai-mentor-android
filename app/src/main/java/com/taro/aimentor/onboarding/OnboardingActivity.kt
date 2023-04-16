package com.taro.aimentor.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.databinding.OnboardingPageBinding
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.util.UIUtil

class OnboardingActivity: AppCompatActivity() {

    companion object {

        const val NUM_QUESTIONS = 3f
    }

    private lateinit var binding: OnboardingPageBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var fragmentController: OnboardingFragmentController
    private var questionNumber = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager.getInstance(this)
        fragmentController = OnboardingFragmentController(
            fragmentManager = supportFragmentManager,
            containerId = R.id.container
        )
        fragmentController.onStateChange(newState = OnboardingAskState.OCCUPATION)
        setProgress()

        bindSubmitButton()
    }

    private fun setProgress() {
        val progress = (questionNumber / NUM_QUESTIONS) * 100f
        binding.progressBar.setProgressCompat(progress.toInt(), true)
    }

    private fun bindSubmitButton() {
        binding.submitButton.setOnClickListener {
            if (!isInputValid()) {
                return@setOnClickListener
            }

            questionNumber += 1
            setProgress()
            fragmentController.onStateChange(newState = OnboardingAskState.YEARS_OF_EXPERIENCE)
        }
    }

    private fun isInputValid(): Boolean {
        when (fragmentController.currentState) {
            OnboardingAskState.OCCUPATION -> {
                if (preferencesManager.occupation.isBlank()) {
                    UIUtil.showLongToast(
                        stringId = R.string.no_occupation_error_message,
                        context = this
                    )
                    return false
                }
                return true
            }
            OnboardingAskState.YEARS_OF_EXPERIENCE -> {
                if (preferencesManager.yearsOfExperience < 0) {
                    UIUtil.showLongToast(
                        stringId = R.string.no_yoe_error_message,
                        context = this
                    )
                    return false
                }
                return true
            }
            else -> return false
        }
    }
}
