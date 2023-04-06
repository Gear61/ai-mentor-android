package com.taro.aimentor.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.util.UIUtil

class MainActivity : AppCompatActivity(), RestClient.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var restClient: RestClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restClient = RestClient(listener = this)
        bindComposer()
    }

    private fun bindComposer() {
        binding.sendMessageButton.setOnClickListener {

        }
    }

    override fun onResponseFetched(response: String) {
        Toast.makeText(this, response, Toast.LENGTH_LONG).show()
    }

    override fun onResponseFailure() {
    }
}
