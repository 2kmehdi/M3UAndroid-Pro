package com.m3u.smartphone

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.m3u.smartphone.ui.App
import com.m3u.smartphone.ui.AppViewModel
import com.m3u.smartphone.ui.business.cast.SharedLinkManager
import com.m3u.smartphone.ui.common.helper.Helper
import com.m3u.smartphone.ui.common.internal.Toolkit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AppViewModel by viewModels()

    private val helper: Helper = Helper(this)

    override fun onResume() {
        super.onResume()
        helper.applyConfiguration()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        helper.applyConfiguration()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            Toolkit(helper) {
                App(
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val dataString = intent.dataString
        val extraText = intent.getStringExtra(Intent.EXTRA_TEXT)

        val candidate = when (action) {
            Intent.ACTION_SEND -> {
                extraText?.let { extractUrl(it) } ?: dataString
            }
            Intent.ACTION_VIEW -> {
                dataString ?: extraText?.let { extractUrl(it) }
            }
            else -> null
        }

        candidate?.let { url ->
            SharedLinkManager.onNewSharedLink(url)
        }
    }

    private fun extractUrl(text: String): String {
        val words = text.split("\\s+".toRegex())
        return words.find {
            it.startsWith("http://", ignoreCase = true) ||
            it.startsWith("https://", ignoreCase = true) ||
            it.startsWith("rtmp://", ignoreCase = true) ||
            it.startsWith("rtsp://", ignoreCase = true)
        } ?: text.trim()
    }
}
