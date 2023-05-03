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
        binding.settingsButton.setOnClickListener {
            setCurrentlySelected(R.id.settings_button)
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
            R.id.settings_button -> {
                binding.settingsIcon.setTextColor(selectedItemColor)
                binding.settingsText.setTextColor(selectedItemColor)
                listener!!.onNavItemSelected(R.id.settings_button)
            }
        }
    }

    private fun unselectCurrentItem() {
        when (currentlySelectedId) {
            R.id.chat_button -> {
                binding.chatIcon.setTextColor(unselectedItemColor)
                binding.chatText.setTextColor(unselectedItemColor)
            }
            R.id.settings_button -> {
                binding.settingsIcon.setTextColor(unselectedItemColor)
                binding.settingsText.setTextColor(unselectedItemColor)
            }
        }
    }
}
