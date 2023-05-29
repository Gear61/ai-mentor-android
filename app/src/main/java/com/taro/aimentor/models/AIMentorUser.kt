package com.taro.aimentor.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.taro.aimentor.common.*
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AIMentorUser(
    @get:PropertyName(USER_ID_KEY)
    @set:PropertyName(USER_ID_KEY)
    var userId: String = "",

    @get:PropertyName(DISPLAY_NAME_KEY)
    @set:PropertyName(DISPLAY_NAME_KEY)
    var displayName: String = "",

    @get:PropertyName(EMAIL_KEY)
    @set:PropertyName(EMAIL_KEY)
    var email: String = "",

    @get:PropertyName(PHOTO_URL_KEY)
    @set:PropertyName(PHOTO_URL_KEY)
    var photoUrl: String = "",

    @get:PropertyName(OCCUPATION_KEY)
    @set:PropertyName(OCCUPATION_KEY)
    var occupation: String = "",

    @get:PropertyName(YEARS_OF_EXPERIENCE_KEY)
    @set:PropertyName(YEARS_OF_EXPERIENCE_KEY)
    var yearsOfExperience: Int = 0,

    @get:PropertyName(FIELD_OF_STUDY_KEY)
    @set:PropertyName(FIELD_OF_STUDY_KEY)
    var fieldOfStudy: String = "",

    @get:PropertyName(IS_INTERVIEWING_KEY)
    @set:PropertyName(IS_INTERVIEWING_KEY)
    var isInterviewing: Boolean = false
) : Parcelable
