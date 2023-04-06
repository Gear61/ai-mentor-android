package com.taro.aimentor.util

import android.app.Activity
import android.content.Context
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import com.joanzapata.iconify.Icon
import com.joanzapata.iconify.IconDrawable
import com.taro.aimentor.R

object UIUtil {

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun loadMenuIcon(menu: Menu, itemId: Int, icon: Icon?, context: Context?) {
        menu.findItem(itemId).icon = IconDrawable(context, icon)
            .colorRes(R.color.white)
            .actionBarSize()
    }

    fun showShortToast(@StringRes stringId: Int, context: Context) {
        showToast(stringId, Toast.LENGTH_SHORT, context)
    }

    fun showLongToast(@StringRes stringId: Int, context: Context) {
        showToast(stringId, Toast.LENGTH_LONG, context)
    }

    private fun showToast(@StringRes stringId: Int, duration: Int, context: Context) {
        Toast.makeText(context, stringId, duration).show()
    }
}
