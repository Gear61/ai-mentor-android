package com.taro.aimentor.home

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.taro.aimentor.R
import com.taro.aimentor.chat.HomeChatFragment
import com.taro.aimentor.profile.ProfileFragment
import com.taro.aimentor.talk.HomeTalkFragment

internal class HomepageFragmentController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    companion object {
        private const val HOME_CHAT_FRAGMENT_NAME = "home_chat_fragment"
        private const val HOME_TALK_FRAGMENT_NAME = "home_talk_fragment"
        private const val PROFILE_FRAGMENT_NAME = "profile_fragment"
    }

    private var homeChatFragment: HomeChatFragment? = null
    private var homeTalkFragment: HomeTalkFragment? = null
    private var profileFragment: ProfileFragment? = null

    @IdRes
    var currentViewId = 0

    fun onNavItemSelected(@IdRes viewId: Int) {
        if (currentViewId == viewId) {
            return
        }
        when (currentViewId) {
            R.id.chat_button -> hideFragment(homeChatFragment!!)
            R.id.talk_button -> hideFragment(homeTalkFragment!!)
            R.id.profile_button -> hideFragment(profileFragment!!)
        }
        currentViewId = viewId
        when (viewId) {
            R.id.chat_button -> if (homeChatFragment == null) {
                homeChatFragment = HomeChatFragment.getInstance()
                addFragment(homeChatFragment)
            } else {
                showFragment(homeChatFragment!!)
            }
            R.id.talk_button -> if (homeTalkFragment == null) {
                homeTalkFragment = HomeTalkFragment.getInstance()
                addFragment(homeTalkFragment)
            } else {
                showFragment(homeTalkFragment!!)
            }
            R.id.profile_button -> if (profileFragment == null) {
                profileFragment = ProfileFragment.getInstance()
                addFragment(profileFragment)
            } else {
                showFragment(profileFragment!!)
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

    fun saveFragments(outState: Bundle) {
        if (homeChatFragment != null) {
            fragmentManager.putFragment(outState, HOME_CHAT_FRAGMENT_NAME, homeChatFragment!!)
        }
        if (homeTalkFragment != null) {
            fragmentManager.putFragment(outState, HOME_TALK_FRAGMENT_NAME, homeTalkFragment!!)
        }
        if (profileFragment != null) {
            fragmentManager.putFragment(outState, PROFILE_FRAGMENT_NAME, profileFragment!!)
        }
    }

    fun restoreFragments(savedInstanceState: Bundle) {
        val savedHomeFragment = fragmentManager.getFragment(savedInstanceState, HOME_CHAT_FRAGMENT_NAME)
        if (savedHomeFragment != null) {
            homeChatFragment = savedHomeFragment as HomeChatFragment
        }

        val savedTalkFragment = fragmentManager.getFragment(savedInstanceState, HOME_TALK_FRAGMENT_NAME)
        if (savedTalkFragment != null) {
            homeTalkFragment = savedTalkFragment as HomeTalkFragment
        }

        val savedProfileFragment = fragmentManager.getFragment(savedInstanceState, PROFILE_FRAGMENT_NAME)
        if (savedProfileFragment != null) {
            profileFragment = savedProfileFragment as ProfileFragment
        }
    }
}
