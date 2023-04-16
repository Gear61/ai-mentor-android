package com.taro.aimentor.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal class OnboardingFragmentController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    companion object {

        const val OCCUPATION_TAG = "OCCUPATION"
        const val FIELD_OF_STUDY_TAG = "FIELD_OF_STUDY"
        const val YEARS_OF_EXPERIENCE_TAG = "YEARS_OF_EXPERIENCE"
        const val IS_INTERVIEWING_TAG = "IS_INTERVIEWING"
    }

    var currentState = OnboardingAskState.NONE

    fun onStateChange(newState: OnboardingAskState) {
        removeCurrentFragment()
        currentState = newState

        when (newState) {
            OnboardingAskState.OCCUPATION -> {
                addFragment(OccupationFormFragment.getInstance())
            }
            OnboardingAskState.FIELD_OF_STUDY -> {
                addFragment(FieldOfStudyFragment.getInstance())
            }
            OnboardingAskState.YEARS_OF_EXPERIENCE -> {
                addFragment(YearsOfExperienceFragment.getInstance())
            }
            OnboardingAskState.INTERVIEW_STATUS -> {

            }
            OnboardingAskState.NONE -> {}
        }
    }

    private fun addFragment(fragment: Fragment?) {
        fragmentManager
            .beginTransaction()
            .add(containerId, fragment!!, getTagForCurrentState())
            .commit()
    }

    private fun getTagForCurrentState(): String {
        return when (currentState) {
            OnboardingAskState.OCCUPATION -> OCCUPATION_TAG
            OnboardingAskState.FIELD_OF_STUDY -> FIELD_OF_STUDY_TAG
            OnboardingAskState.YEARS_OF_EXPERIENCE -> YEARS_OF_EXPERIENCE_TAG
            OnboardingAskState.INTERVIEW_STATUS -> IS_INTERVIEWING_TAG
            OnboardingAskState.NONE -> error("No fragment tag for NONE state!")
        }
    }

    private fun removeCurrentFragment() {
        if (currentState == OnboardingAskState.NONE) {
            return
        }

        val fragmentToRemove = fragmentManager.findFragmentByTag(getTagForCurrentState())
        if (fragmentToRemove != null) {
            fragmentManager.beginTransaction().remove(fragmentToRemove).commit();
        }
    }
}
