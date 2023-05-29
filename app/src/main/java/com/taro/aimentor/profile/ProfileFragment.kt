package com.taro.aimentor.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.taro.aimentor.R
import com.taro.aimentor.common.PRIVACY_POLICY_URL
import com.taro.aimentor.common.TERMS_AND_CONDITIONS_URL
import com.taro.aimentor.databinding.ProfileBinding
import com.taro.aimentor.onboarding.OnboardingActivity
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.profile.ProfileItem.ClickActionType.*
import com.taro.aimentor.theme.ThemeManager
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.UIUtil
import com.taro.aimentor.web.WebActivity

class ProfileFragment: Fragment(), ProfileAdapter.Listener, SetDisplayNameDialog.Listener {

    companion object {

        fun getInstance(): ProfileFragment {
            return ProfileFragment()
        }

        const val SUPPORT_EMAIL = "team@jointaro.com"
    }

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var setNameDialog: SetDisplayNameDialog
    private lateinit var adapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager.getInstance(view.context)
        setNameDialog = SetDisplayNameDialog(
            context = view.context,
            listener = this
        )
        val profileItems = ProfileDataModel.generateItems(
            preferencesManager = preferencesManager,
            context = view.context
        )
        adapter = ProfileAdapter(
            listItems = profileItems,
            listener = this
        )
        binding.settingsOptions.adapter = adapter
    }

    override fun onDisplayNameClicked() {
        setNameDialog.show(currentName = preferencesManager.userDisplayName!!)
    }

    override fun onNewNameSubmitted(newName: String) {
        preferencesManager.onDisplayNameChanged(newName = newName)
        adapter.onDisplayNameChanged(newName = newName)
    }

    override fun onProfileItemClicked(profileItem: ProfileItem) {
        when (profileItem.clickActionType) {
            SEND_FEEDBACK -> sendFeedback()
            RATE_APP -> rateApp()
            SHARE_APP -> shareApp()
            TERMS_AND_CONDITIONS -> openTermsAndConditions()
            PRIVACY_POLICY -> openPrivacyPolicy()
            LOG_OUT -> logOut()
            else -> {}
        }
    }

    override fun setDarkMode(isToggled: Boolean) {
        val themeMode = if (isToggled) ThemeMode.DARK else ThemeMode.LIGHT
        preferencesManager.themeMode = themeMode
        ThemeManager.applyTheme(themeMode)
    }

    private fun sendFeedback() {
        val uriText =
            "mailto:${SUPPORT_EMAIL}?subject=" + Uri.encode(getString(R.string.feedback_subject))
        val mailUri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO, mailUri)
        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_email)))
        requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay)
    }

    private fun rateApp() {
        val uri = Uri.parse("market://details?id=${requireContext().packageName}")
        val rateIntent = Intent(Intent.ACTION_VIEW, uri)
        if (requireContext().packageManager.queryIntentActivities(rateIntent, 0).size <= 0) {
            UIUtil.showLongToast(
                stringId = R.string.play_store_error,
                context = requireContext()
            )
        }
        startActivity(rateIntent)
    }

    private fun shareApp() {
        val shareIntent = ShareCompat.IntentBuilder(requireActivity())
            .setType("text/plain")
            .setText(getString(R.string.share_app_message))
            .intent
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(shareIntent)
            requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay)
        }
    }

    private fun openTermsAndConditions() {
        openWebActivity(
            title = getString(R.string.terms_and_conditions),
            url = TERMS_AND_CONDITIONS_URL
        )
    }

    private fun openPrivacyPolicy() {
        openWebActivity(
            title = getString(R.string.privacy_policy),
            url = PRIVACY_POLICY_URL
        )
    }

    private fun logOut() {
        // preferencesManager.logOut()

        val loginIntent = Intent(requireActivity(), OnboardingActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }

    private fun openWebActivity(title: String, url: String) {
        val intent = Intent(requireActivity(), WebActivity::class.java)
        intent.putExtra(WebActivity.TITLE_KEY, title)
        intent.putExtra(WebActivity.URL_KEY, url)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay)
    }
}
