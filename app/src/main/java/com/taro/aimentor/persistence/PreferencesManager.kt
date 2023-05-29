package com.taro.aimentor.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taro.aimentor.common.*
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.SingletonHolder

class PreferencesManager private constructor(context: Context) {

    companion object : SingletonHolder<PreferencesManager, Context>(::PreferencesManager) {

        const val THEME_MODE_KEY = "theme_mode"
        const val NUM_APP_OPENS_KEY = "num_app_opens"

        const val APP_OPENS_FOR_RATING_UPSELL = 5
    }

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var themeMode: Int
        get() = prefs.getInt(THEME_MODE_KEY, ThemeMode.FOLLOW_SYSTEM)
        set(newThemeMode) {
            prefs.edit().putInt(THEME_MODE_KEY, newThemeMode).apply()
        }

    var occupation: String
        get() = prefs.getString(OCCUPATION_KEY, "") ?: ""
        set(newOccupation) {
            prefs.edit().putString(OCCUPATION_KEY, newOccupation).apply()
        }

    var fieldOfStudy: String
        get() = prefs.getString(FIELD_OF_STUDY_KEY, "") ?: ""
        set(newFieldOfStudy) {
            prefs.edit().putString(FIELD_OF_STUDY_KEY, newFieldOfStudy).apply()
        }

    var yearsOfExperience: Int
        get() = prefs.getInt(YEARS_OF_EXPERIENCE_KEY, -1)
        set(newYearsOfExperience) {
            prefs.edit().putInt(YEARS_OF_EXPERIENCE_KEY, newYearsOfExperience).apply()
        }

    var isInterviewing: Boolean
        get() = prefs.getBoolean(IS_INTERVIEWING_KEY, true)
        set(newIsInterviewing) {
            prefs.edit().putBoolean(IS_INTERVIEWING_KEY, newIsInterviewing).apply()
        }

    var userId: String
        get() = prefs.getString(USER_ID_KEY, "") ?: ""
        set(newUserId) {
            prefs.edit().putString(USER_ID_KEY, newUserId).apply()
        }

    var userDisplayName: String
        get() = prefs.getString(DISPLAY_NAME_KEY, "") ?: ""
        set(newUserDisplayName) {
            prefs.edit().putString(DISPLAY_NAME_KEY, newUserDisplayName).apply()
        }

    var userPhotoUrl: String
        get() = prefs.getString(PHOTO_URL_KEY, "") ?: ""
        set(newUserPhotoUrl) {
            prefs.edit().putString(PHOTO_URL_KEY, newUserPhotoUrl).apply()
        }

    var userEmail: String
        get() = prefs.getString(EMAIL_KEY, "") ?: ""
        set(newUserEmail) {
            prefs.edit().putString(EMAIL_KEY, newUserEmail).apply()
        }

    fun onDisplayNameChanged(newName: String) {
        userDisplayName = newName

        val usersCollection = Firebase.firestore.collection(USERS_COLLECTION)
        val userDoc = usersCollection.document(userId)
        userDoc.update(DISPLAY_NAME_KEY, newName)
    }

    fun logAppOpenAndCheckForRatingUpsell(): Boolean {
        val currentAppOpens = prefs.getInt(NUM_APP_OPENS_KEY, 0) + 1
        prefs.edit().putInt(NUM_APP_OPENS_KEY, currentAppOpens).apply()
        return currentAppOpens == APP_OPENS_FOR_RATING_UPSELL
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        userId = ""
        userDisplayName = ""
        userEmail = ""
        userPhotoUrl = ""
        occupation = ""
        yearsOfExperience = -1
        fieldOfStudy = ""
        isInterviewing = true
    }
}
