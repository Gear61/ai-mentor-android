package com.taro.aimentor.onboarding

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.taro.aimentor.R
import com.taro.aimentor.common.PRIVACY_POLICY_URL
import com.taro.aimentor.common.TERMS_AND_CONDITIONS_URL
import com.taro.aimentor.databinding.OnboardingActivityBinding
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.onboarding.personalization.PersonalizationActivity
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.util.StringUtil
import com.taro.aimentor.util.UIUtil
import com.taro.aimentor.views.ProgressDialog
import com.taro.aimentor.web.WebActivity
import java.util.*

class OnboardingActivity : AppCompatActivity(), OnboardingManager.Listener {

    private lateinit var binding: OnboardingActivityBinding
    private lateinit var onboardingManager: OnboardingManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var progressDialog: ProgressDialog
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingManager = OnboardingManager(this, this)
        preferencesManager = PreferencesManager.getInstance(this)

        binding.googleButton.setOnClickListener {
            onboardingManager.loginWithGoogle()
        }
        bindEmailViews()
        setUpDisclaimerText()

        progressDialog = ProgressDialog(this)
    }

    private fun setUpDisclaimerText() {
        val termsAndConditions = getString(R.string.terms_and_conditions)
        val privacyPolicy = getString(R.string.privacy_policy)
        val finalDisclaimerText = getString(
            R.string.disclaimer_text,
            termsAndConditions,
            privacyPolicy
        )
        val disclaimerTextWithClickables = SpannableString(finalDisclaimerText)

        val termsAndConditionsStart = finalDisclaimerText.indexOf(termsAndConditions)
        val termsAndConditionsSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebActivity(termsAndConditions, TERMS_AND_CONDITIONS_URL)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        disclaimerTextWithClickables.setSpan(
            termsAndConditionsSpan,
            termsAndConditionsStart,
            termsAndConditionsStart + termsAndConditions.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val privacyPolicyStart = finalDisclaimerText.indexOf(privacyPolicy)
        val privacyPolicySpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebActivity(privacyPolicy, PRIVACY_POLICY_URL)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        disclaimerTextWithClickables.setSpan(
            privacyPolicySpan,
            privacyPolicyStart,
            privacyPolicyStart + privacyPolicy.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.disclaimerText.text = disclaimerTextWithClickables
        binding.disclaimerText.movementMethod = LinkMovementMethod.getInstance();
    }

    private fun bindEmailViews() {
        binding.emailAddressInput.doAfterTextChanged {
            if (binding.emailAddressInput.length() == 0) {
                binding.clearEmailButton.visibility = View.GONE
            } else {
                binding.clearEmailButton.visibility = View.VISIBLE
            }
        }
        binding.clearEmailButton.setOnClickListener {
            binding.emailAddressInput.setText("")
        }
        binding.viewPasswordButton.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                binding.passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.viewPasswordButton.setText(R.string.disabled_eye_icon)
            } else {
                binding.passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.viewPasswordButton.setText(R.string.eye_icon)
            }
            binding.passwordInput.setSelection(binding.passwordInput.text.length)
        }
        binding.emailContinueButton.setOnClickListener {
            UIUtil.hideKeyboard(this)

            val email = binding.emailAddressInput.text.toString().toLowerCase(Locale.ROOT).trim()
            if (!StringUtil.isValidEmail(email)) {
                UIUtil.showLongToast(R.string.email_address_invalid_error_message, this)
                return@setOnClickListener
            }
            val password = binding.passwordInput.text.toString()
            if (password.isBlank()) {
                UIUtil.showLongToast(R.string.blank_password_error_message, this)
                return@setOnClickListener
            }

            onboardingManager.loginWithEmail(
                email = email,
                password = password
            )
        }
    }

    private fun openWebActivity(title: String, url: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.TITLE_KEY, title)
        intent.putExtra(WebActivity.URL_KEY, url)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay)
    }

    override fun onLoginStarted(progressMessageId: Int) {
        progressDialog.show(progressMessageId)
    }

    private fun openPersonalizationFlow() {
        val intent = Intent(this, PersonalizationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.stay)
        finish()
    }

    override fun onAuthSuccessful() {
        progressDialog.dismiss()
        if (preferencesManager.occupation.isBlank()) {
            openPersonalizationFlow()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onLoginFailure(errorMessageResId: Int) {
        progressDialog.dismiss()
        UIUtil.showLongToast(errorMessageResId, this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onboardingManager.onActivityResult(
            requestCode = requestCode,
            data = data
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        onboardingManager.cleanUp()
        progressDialog.dismiss()
    }
}
