package com.taro.aimentor.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.taro.aimentor.R

class BetterRadioGroup @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), BetterRadioButton.Listener {

    private val radioButtons: MutableList<BetterRadioButton> = ArrayList()

    init {
        orientation = VERTICAL
    }

    fun setSize(numButtons: Int) {
        for (button in radioButtons) {
            button.setListener(null)
        }
        radioButtons.clear()
        removeAllViews()

        for (i in 0 until numButtons) {
            val radioButton = BetterRadioButton(context)
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            val spacing: Int = resources.getDimensionPixelSize(R.dimen.radio_group_button_spacing)
            params.setMargins(0, spacing, 0, spacing)
            radioButton.layoutParams = params
            radioButtons.add(radioButton)
            radioButton.setListener(this)
            radioButton.setIndex(i)
            addView(radioButton)
        }
    }

    val checkedButton: BetterRadioButton?
        get() {
            for (radioButton in radioButtons) {
                if (radioButton.isChecked) {
                    return radioButton
                }
            }
            return null
        }

    fun getRadioButton(index: Int): BetterRadioButton {
        return radioButtons[index]
    }

    override fun onChecked(index: Int) {
        for (i in radioButtons.indices) {
            if (i != index) {
                radioButtons[i].isChecked = false
            }
        }
    }
}
