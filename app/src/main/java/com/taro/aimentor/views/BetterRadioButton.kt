package com.taro.aimentor.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.taro.aimentor.R
import com.taro.aimentor.util.UIUtil

class BetterRadioButton @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    interface Listener {
        fun onChecked(index: Int)
    }

    private val radioButton: RadioButton
    private val textView: TextView

    init {
        inflate(getContext(), R.layout.better_radio_button, this)
        radioButton = findViewById(R.id.radio_button)
        textView = findViewById(R.id.radio_button_text)

        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundResource(R.drawable.non_selected_radio_button)
        val padding = getContext().resources.getDimensionPixelSize(R.dimen.radio_button_padding)
        setPadding(0, padding, padding, padding)
        setBackground()
        setOnClickListener(this)
        radioButton.setOnCheckedChangeListener(this)
    }

    private var index = 0
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    var isChecked: Boolean
        get() = radioButton.isChecked
        set(isChecked) {
            radioButton.isChecked = isChecked
        }

    var text: String
        get() = textView.text.toString()
        set(text) {
            textView.text = text
        }

    override fun onClick(view: View) {
        if (!radioButton.isChecked) {
            radioButton.isChecked = true
            setBackground()
        }
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        setBackground()
        if (radioButton.isChecked && listener != null) {
            listener!!.onChecked(index)
        }
    }

    private fun setBackground() {
        if (radioButton.isChecked) {
            setBackgroundResource(R.drawable.radio_button_border)
        } else {
            setBackgroundResource(R.drawable.non_selected_radio_button)
        }
    }
}
