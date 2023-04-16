package com.taro.aimentor.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.databinding.OnboardingPageBinding
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.util.UIUtil

class OnboardingActivity: AppCompatActivity() {

    companion object {

        const val NUM_QUESTIONS = 3f
    }

    private lateinit var binding: OnboardingPageBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var fragmentController: OnboardingFragmentController
    private var questionNumber = 1

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
        val progress = (questionNumber.toFloat() / NUM_QUESTIONS) * 100f
        binding.progressBar.setProgressCompat(progress.toInt(), true)
    }

    private fun bindSubmitButton() {
        binding.submitButton.setOnClickListener {
            if (!isInputValid()) {
                return@setOnClickListener
            }

            when (questionNumber) {
                1 -> {
                    if (preferencesManager.occupation == getString(R.string.student)) {
                        fragmentController.onStateChange(newState = OnboardingAskState.FIELD_OF_STUDY)
                    } else {
                        fragmentController.onStateChange(newState = OnboardingAskState.YEARS_OF_EXPERIENCE)
                    }
                }
                2 -> {
                    fragmentController.onStateChange(newState = OnboardingAskState.INTERVIEW_STATUS)
                }
                3 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            questionNumber += 1
            setProgress()
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
            OnboardingAskState.FIELD_OF_STUDY -> {
                if (preferencesManager.fieldOfStudy.isBlank()) {
                    UIUtil.showLongToast(
                        stringId = R.string.no_field_of_study_error_message,
                        context = this
                    )
                    return false
                }
                return true
            }
            else -> return true
        }
    }
}
