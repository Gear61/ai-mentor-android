package com.taro.aimentor.home

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.taro.aimentor.R
import com.taro.aimentor.chat.HomeChatFragment
import com.taro.aimentor.settings.SettingsFragment

internal class HomepageFragmentController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    private var homeChatFragment: HomeChatFragment? = null
    private var settingsFragment: SettingsFragment? = null

    @IdRes
    var currentViewId = 0

    fun onNavItemSelected(@IdRes viewId: Int) {
        if (currentViewId == viewId) {
            return
        }
        when (currentViewId) {
            R.id.chat_button -> hideFragment(homeChatFragment!!)
            R.id.settings_button -> hideFragment(settingsFragment!!)
        }
        currentViewId = viewId
        when (viewId) {
            R.id.chat_button -> if (homeChatFragment == null) {
                homeChatFragment = HomeChatFragment.getInstance()
                addFragment(homeChatFragment)
            } else {
                showFragment(homeChatFragment!!)
            }
            R.id.settings_button -> if (settingsFragment == null) {
                settingsFragment = SettingsFragment.getInstance()
                addFragment(settingsFragment)
            } else {
                showFragment(settingsFragment!!)
            }
            else -> loadHomeInitially()
        }
    }

    private fun loadHomeInitially() {
        onNavItemSelected(R.id.chat_button)
    }

    private fun addFragment(fragment: Fragment?) {
        fragmentManager.beginTransaction().add(containerId, fragment!!).commit()
    }

    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().show(fragment).commit()
    }

    private fun hideFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().hide(fragment).commit()
    }

    fun clearFragments() {
        for (fragment in fragmentManager.fragments) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
    }
}
