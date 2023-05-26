package com.taro.aimentor.onboarding.personalization

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taro.aimentor.R
import com.taro.aimentor.common.*
import com.taro.aimentor.databinding.OnboardingPageBinding
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.util.StringUtil
import com.taro.aimentor.util.UIUtil

class PersonalizationActivity: AppCompatActivity() {

    companion object {

        const val NUM_QUESTIONS = 3f
    }

    private lateinit var binding: OnboardingPageBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var fragmentController: PersonalizationFragmentController
    private var questionNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager.getInstance(this)
        fragmentController = PersonalizationFragmentController(
            fragmentManager = supportFragmentManager,
            containerId = R.id.container
        )
        fragmentController.onStateChange(newState = PersonalizationAskState.OCCUPATION)
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
                    if (preferencesManager.occupation.lowercase() == getString(R.string.student)) {
                        fragmentController.onStateChange(newState = PersonalizationAskState.FIELD_OF_STUDY)
                    } else {
                        fragmentController.onStateChange(newState = PersonalizationAskState.YEARS_OF_EXPERIENCE)
                    }
                }
                2 -> {
                    fragmentController.onStateChange(newState = PersonalizationAskState.INTERVIEW_STATUS)
                }
                3 -> {
                    syncCareerInformation()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            questionNumber += 1
            setProgress()
        }
    }

    private fun syncCareerInformation() {
        val usersCollection = Firebase.firestore.collection(USERS_COLLECTION)
        val userDoc = usersCollection.document(preferencesManager.userId)
        val updates = hashMapOf<String, Any>(
            OCCUPATION_KEY to StringUtil.capitalizeWords(input = preferencesManager.occupation),
            YEARS_OF_EXPERIENCE_KEY to preferencesManager.yearsOfExperience,
            FIELD_OF_STUDY_KEY to StringUtil.capitalizeWords(input = preferencesManager.fieldOfStudy),
            IS_INTERVIEWING_KEY to preferencesManager.isInterviewing
        )
        userDoc.update(updates)
    }

    private fun isInputValid(): Boolean {
        when (fragmentController.currentState) {
            PersonalizationAskState.OCCUPATION -> {
                if (preferencesManager.occupation.isBlank()) {
                    UIUtil.showLongToast(
                        stringId = R.string.no_occupation_error_message,
                        context = this
                    )
                    return false
                }
                return true
            }
            PersonalizationAskState.YEARS_OF_EXPERIENCE -> {
                if (preferencesManager.yearsOfExperience < 0) {
                    UIUtil.showLongToast(
                        stringId = R.string.no_yoe_error_message,
                        context = this
                    )
                    return false
                }
                return true
            }
            PersonalizationAskState.FIELD_OF_STUDY -> {
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
