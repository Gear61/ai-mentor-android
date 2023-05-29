package com.taro.aimentor.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.StringRes
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.taro.aimentor.R
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.html.HtmlPlugin

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

    fun loadMenuIcon(
        menu: Menu,
        itemId: Int,
        icon: IIcon,
        context: Context
    ) {
        menu.findItem(itemId).icon = IconicsDrawable(context, icon).apply {
            colorInt = Color.WHITE
            sizeDp = 24
        }
    }

    fun showShortToast(
        @StringRes stringId: Int,
        context: Context
    ) {
        showToast(stringId, Toast.LENGTH_SHORT, context)
    }

    fun showLongToast(
        @StringRes stringId: Int,
        context: Context
    ) {
        showToast(stringId, Toast.LENGTH_LONG, context)
    }

    private fun showToast(@StringRes stringId: Int, duration: Int, context: Context) {
        Toast.makeText(context, stringId, duration).show()
    }

    fun getMarkwonInstance(
        context: Context
    ): Markwon {
        return Markwon
            .builder(context)
            .usePlugin(HtmlPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    val bulletWidthPx = context.resources.getDimensionPixelSize(R.dimen.markdown_bullet_width)
                    builder.bulletWidth(bulletWidthPx)
                }
            })
            .build()
    }

    fun setCheckedImmediately(checkableView: CompoundButton, checked: Boolean) {
        checkableView.isChecked = checked
        checkableView.jumpDrawablesToCurrentState()
    }
}
