package com.taro.aimentor.onboarding.personalization

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal class PersonalizationFragmentController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    companion object {

        const val OCCUPATION_TAG = "OCCUPATION"
        const val FIELD_OF_STUDY_TAG = "FIELD_OF_STUDY"
        const val YEARS_OF_EXPERIENCE_TAG = "YEARS_OF_EXPERIENCE"
        const val IS_INTERVIEWING_TAG = "IS_INTERVIEWING"
    }

    var currentState = PersonalizationAskState.NONE

    fun onStateChange(newState: PersonalizationAskState) {
        removeCurrentFragment()
        currentState = newState

        when (newState) {
            PersonalizationAskState.OCCUPATION -> {
                addFragment(OccupationFormFragment.getInstance())
            }
            PersonalizationAskState.FIELD_OF_STUDY -> {
                addFragment(FieldOfStudyFragment.getInstance())
            }
            PersonalizationAskState.YEARS_OF_EXPERIENCE -> {
                addFragment(YearsOfExperienceFragment.getInstance())
            }
            PersonalizationAskState.INTERVIEW_STATUS -> {
                addFragment(InterviewStatusFragment.getInstance())
            }
            PersonalizationAskState.NONE -> {}
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
            PersonalizationAskState.OCCUPATION -> OCCUPATION_TAG
            PersonalizationAskState.FIELD_OF_STUDY -> FIELD_OF_STUDY_TAG
            PersonalizationAskState.YEARS_OF_EXPERIENCE -> YEARS_OF_EXPERIENCE_TAG
            PersonalizationAskState.INTERVIEW_STATUS -> IS_INTERVIEWING_TAG
            PersonalizationAskState.NONE -> error("No fragment tag for NONE state!")
        }
    }

    private fun removeCurrentFragment() {
        if (currentState == PersonalizationAskState.NONE) {
            return
        }

        val fragmentToRemove = fragmentManager.findFragmentByTag(getTagForCurrentState())
        if (fragmentToRemove != null) {
            fragmentManager.beginTransaction().remove(fragmentToRemove).commit();
        }
    }
}
