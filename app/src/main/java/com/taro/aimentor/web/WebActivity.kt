package com.taro.aimentor.web

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.taro.aimentor.R
import com.taro.aimentor.databinding.WebViewBinding
import com.taro.aimentor.util.ClipboardUtil
import com.taro.aimentor.util.UIUtil

open class WebActivity : AppCompatActivity() {

    companion object {
        const val TITLE_KEY = "title"
        const val URL_KEY = "url"
    }

    private lateinit var pageTitle: String
    private lateinit var url: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = WebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_close).apply {
                colorInt = Color.WHITE
                sizeDp = 24
            }
        )

        url = intent.getStringExtra(URL_KEY)!!
        title = intent.getStringExtra(TITLE_KEY)!!
        pageTitle = intent.getStringExtra(TITLE_KEY)!!

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.builtInZoomControls = true
        binding.webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webview.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        binding.webview.loadUrl(url)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.slide_out_through_bottom)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_view_menu, menu)
        UIUtil.loadMenuIcon(
            menu!!,
            R.id.copy_link,
            MaterialDesignIconic.Icon.gmi_link,
            this
        )
        UIUtil.loadMenuIcon(
            menu,
            R.id.share_link,
            MaterialDesignIconic.Icon.gmi_share,
            this
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.copy_link -> {
                ClipboardUtil.copyTextToClipboard(
                    textToCopy = url,
                    label = pageTitle,
                    context = this
                )
                UIUtil.showShortToast(
                    stringId = R.string.link_copied_to_clipboard,
                    context = this
                )
                return true
            }
            R.id.share_link -> {
                val shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(url)
                    .intent
                if (shareIntent.resolveActivity(packageManager) != null) {
                    startActivity(shareIntent)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
