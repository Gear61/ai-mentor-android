package com.taro.aimentor.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.SingletonHolder

class PreferencesManager private constructor(context: Context) {

    companion object : SingletonHolder<PreferencesManager, Context>(::PreferencesManager) {

        const val THEME_MODE = "theme_mode"
        const val NUM_APP_OPENS = "num_app_opens"
        const val OCCUPATION = "occupation"
        const val YEARS_OF_EXPERIENCE = "years_of_experience"
        const val FIELD_OF_STUDY = "field_of_study"
        const val IS_INTERVIEWING = "is_interviewing"

        const val APP_OPENS_FOR_RATING_UPSELL = 5
    }

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var themeMode: Int
        get() = prefs.getInt(THEME_MODE, ThemeMode.FOLLOW_SYSTEM)
        set(newThemeMode) {
            prefs.edit().putInt(THEME_MODE, newThemeMode).apply()
        }

    var occupation: String
        get() = prefs.getString(OCCUPATION, "") ?: ""
        set(newOccupation) {
            prefs.edit().putString(OCCUPATION, newOccupation).apply()
        }

    var fieldOfStudy: String
        get() = prefs.getString(FIELD_OF_STUDY, "") ?: ""
        set(newFieldOfStudy) {
            prefs.edit().putString(FIELD_OF_STUDY, newFieldOfStudy).apply()
        }

    var yearsOfExperience: Int
        get() = prefs.getInt(YEARS_OF_EXPERIENCE, 0)
        set(newYearsOfExperience) {
            prefs.edit().putInt(YEARS_OF_EXPERIENCE, newYearsOfExperience).apply()
        }

    var isInterviewing: Boolean
        get() = prefs.getBoolean(IS_INTERVIEWING, false)
        set(newIsInterviewing) {
            prefs.edit().putBoolean(IS_INTERVIEWING, newIsInterviewing).apply()
        }

    fun logAppOpenAndCheckForRatingUpsell(): Boolean {
        val currentAppOpens = prefs.getInt(NUM_APP_OPENS, 0) + 1
        prefs.edit().putInt(NUM_APP_OPENS, currentAppOpens).apply()
        return currentAppOpens == APP_OPENS_FOR_RATING_UPSELL
    }
}
