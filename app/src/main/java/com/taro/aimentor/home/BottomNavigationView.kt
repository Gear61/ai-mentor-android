package com.taro.aimentor.home

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.taro.aimentor.R
import com.taro.aimentor.databinding.HomeBottomNavigationBinding

class BottomNavigationView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context!!, attrs, defStyle) {

    interface Listener {
        fun onNavItemSelected(@IdRes viewId: Int)
    }

    private var binding: HomeBottomNavigationBinding

    var unselectedItemColor = ContextCompat.getColor(context!!, R.color.bottom_navigation_item_color)
    var selectedItemColor = ContextCompat.getColor(context!!, R.color.accent_color)

    private var listener: Listener? = null
    private var currentlySelectedId: Int = -1

    init {
        inflate(getContext(), R.layout.home_bottom_navigation, this)
        binding = HomeBottomNavigationBinding.bind(this)

        binding.chatButton.setOnClickListener {
            setCurrentlySelected(R.id.chat_button)
        }
        binding.talkButton.setOnClickListener {
            setCurrentlySelected(R.id.talk_button)
        }
        binding.profileButton.setOnClickListener {
            setCurrentlySelected(R.id.profile_button)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setCurrentlySelected(@IdRes newlySelectedId: Int) {
        if (currentlySelectedId == newlySelectedId) {
            return
        }

        unselectCurrentItem()
        currentlySelectedId = newlySelectedId
        when (newlySelectedId) {
            R.id.chat_button -> {
                binding.chatIcon.setTextColor(selectedItemColor)
                binding.chatText.setTextColor(selectedItemColor)
                listener!!.onNavItemSelected(R.id.chat_button)
            }
            R.id.talk_button -> {
                binding.talkIcon.setTextColor(selectedItemColor)
                binding.talkText.setTextColor(selectedItemColor)
                listener!!.onNavItemSelected(R.id.talk_button)
            }
            R.id.profile_button -> {
                binding.profileIcon.setTextColor(selectedItemColor)
                binding.profileText.setTextColor(selectedItemColor)
                listener!!.onNavItemSelected(R.id.profile_button)
            }
        }
    }

    private fun unselectCurrentItem() {
        when (currentlySelectedId) {
            R.id.chat_button -> {
                binding.chatIcon.setTextColor(unselectedItemColor)
                binding.chatText.setTextColor(unselectedItemColor)
            }
            R.id.talk_button -> {
                binding.talkIcon.setTextColor(unselectedItemColor)
                binding.talkText.setTextColor(unselectedItemColor)
            }
            R.id.profile_button -> {
                binding.profileIcon.setTextColor(unselectedItemColor)
                binding.profileText.setTextColor(unselectedItemColor)
            }
        }
    }
}
