package com.derdan.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val title = TextView(this).apply {
            text = "DerDan"
            textSize = 34f
            setTextColor(Color.parseColor("#7C4DFF"))
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            textSize = 16f
            setPadding(0, 32, 0, 32)
            gravity = Gravity.CENTER
        }

        val enableBtn = Button(this).apply {
            text = "Enable Accessibility"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val refreshBtn = Button(this).apply {
            text = "Refresh Log"
            setOnClickListener { refreshLog() }
        }

        val clearBtn = Button(this).apply {
            text = "Clear Log"
            setOnClickListener {
                File(filesDir, "derdan.log").writeText("")
                refreshLog()
            }
        }

        logView = TextView(this).apply {
            textSize = 11f
            setPadding(0, 32, 0, 0)
            typeface = android.graphics.Typeface.MONOSPACE
        }

        val scroll = ScrollView(this).apply {
            addView(logView)
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(enableBtn)
        layout.addView(refreshBtn)
        layout.addView(clearBtn)
        layout.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(layout)
        refreshStatus()
        refreshLog()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
        refreshLog()
    }

    private fun refreshStatus() {
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )?.contains(packageName) == true
        status.text = if (enabled) "Status: ENABLED ✓" else "Status: not enabled"
    }

    private fun refreshLog() {
        val f = File(filesDir, "derdan.log")
        logView.text = if (f.exists()) f.readText().takeLast(20000)
                       else "No logs yet."
    }
}
