package com.taro.aimentor.onboarding

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taro.aimentor.R
import com.taro.aimentor.common.EMAIL_KEY
import com.taro.aimentor.common.USERS_COLLECTION
import com.taro.aimentor.models.AIMentorUser
import com.taro.aimentor.persistence.PreferencesManager

/**
 * Abstracts away the business logic (non-UI work) for onboarding.
 * Communicates back to the view controller via the listener pattern.
 */
class OnboardingManager(private var activity: Activity?, private var listener: Listener) {

    enum class LoginType {
        GOOGLE,
        EMAIL
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 636
        private const val GAP_MILLIS_NEEDED_FOR_LOGIN = 60000

        private const val DEFAULT_USER_IMAGE_URL = "https://i.ibb.co/X5cFC2j/default-user-image.jpg"
    }

    interface Listener {
        fun onLoginStarted(progressMessageId: Int)

        fun onLoginSuccessful()

        fun onSignUpSuccessful()

        fun onLoginFailure(errorMessageResId: Int)
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    private var preferencesManager: PreferencesManager = PreferencesManager.getInstance(activity!!)

    private val usersCollection = Firebase.firestore.collection(USERS_COLLECTION)
    private var loginType = LoginType.GOOGLE

    init {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity!!.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity!!, googleSignInOptions)
    }

    fun loginWithGoogle() {
        loginType = LoginType.GOOGLE
        val signInIntent = googleSignInClient.signInIntent
        activity!!.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun loginWithEmail(email: String, password: String) {
        loginType = LoginType.EMAIL
        listener.onLoginStarted(R.string.logging_in_email)
        checkForUserWithEmail(
            email = email,
            password = password
        )
    }

    private fun checkForUserWithEmail(email: String, password: String) {
        usersCollection
            .whereEqualTo(EMAIL_KEY, email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            processAuthResult(authTask = task)
                        }
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            processAuthResult(authTask = task)
                        }
                }
            }
    }

    fun onActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The task returned is always in completed state
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                listener.onLoginStarted(R.string.logging_in_google)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(activity!!) { authTask ->
                        processAuthResult(authTask)
                    }
            } catch (authException: ApiException) {
                // The user cancelling the auth flow throws an exception apparently
                if (authException.statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    alertOfGenericError()
                }
            }
        }
    }

    private fun processAuthResult(authTask: Task<AuthResult>) {
        if (authTask.isSuccessful) {
            val user = auth.currentUser
            if (user == null) {
                alertOfGenericError()
                return
            } else {
                val millisSinceAccountCreation =
                    System.currentTimeMillis() - auth.currentUser!!.metadata!!.creationTimestamp

                // Store user data to shared preferences
                preferencesManager.userId = user.uid
                preferencesManager.userDisplayName = user.displayName ?: ""

                if (loginType == LoginType.GOOGLE) {
                    val highResPhotoUrl = user.photoUrl
                        .toString()
                        .replace("s96-c", "s400-c")
                    preferencesManager.userPhotoUrl = highResPhotoUrl
                } else {
                    preferencesManager.userPhotoUrl = DEFAULT_USER_IMAGE_URL
                }
                preferencesManager.userEmail = user.email ?: ""

                if (millisSinceAccountCreation > GAP_MILLIS_NEEDED_FOR_LOGIN) {
                    getUserAndSyncToLocal()
                } else {
                    createUser()
                }
            }
        } else {
            try {
                throw authTask.exception!!
            } catch (exception: Exception) {
                exception.printStackTrace()
                alertOfGenericError()
            }
        }
    }

    private fun createUser() {
        val userDocument = usersCollection.document(preferencesManager.userId)
        val userObject = AIMentorUser(
            userId = preferencesManager.userId,
            displayName = preferencesManager.userDisplayName,
            email = preferencesManager.userEmail,
            photoUrl = preferencesManager.userPhotoUrl,
        )
        userDocument
            .set(userObject)
            .addOnSuccessListener {
                listener.onSignUpSuccessful()
            }
            .addOnFailureListener {
                alertOfGenericError()
            }
    }

    private fun getUserAndSyncToLocal() {
        val userDocument = usersCollection.document(preferencesManager.userId)
        userDocument
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(AIMentorUser::class.java)
                if (user == null) {
                    alertOfGenericError()
                    return@addOnSuccessListener
                }

                preferencesManager.userDisplayName = user.displayName
                preferencesManager.userPhotoUrl = user.photoUrl
                listener.onLoginSuccessful()
            }
            .addOnFailureListener {
                alertOfGenericError()
            }
    }

    private fun alertOfGenericError() {
        when (loginType) {
            LoginType.GOOGLE -> listener.onLoginFailure(R.string.google_login_fail)
            LoginType.EMAIL -> listener.onLoginFailure(R.string.email_login_fail)
        }
    }

    fun cleanUp() {
        activity = null
    }
}
