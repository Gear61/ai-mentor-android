package com.taro.aimentor.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.databinding.SettingsBinding

class SettingsActivity : AppCompatActivity(), SettingsAdapter.Listener {

    private lateinit var binding: SettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsOptions.adapter = SettingsAdapter(
            resources = resources,
            listener = this
        )
    }

    override fun onItemClick(position: Int) {

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
    }
}
