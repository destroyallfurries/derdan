package com.derdan.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DerDanService : AccessibilityService() {

    companion object {
        private const val TAG = "DerDan"
        // Toggle to false once you've spied on StatusAI and know what to click.
        private const val SPY_MODE = true
        private const val TARGET_TEXT = "watch ad"
        private const val CYCLE_DELAY_MS = 2500L
        private const val MAX_LOG_LINES = 500
    }

    private var lastActionTime = 0L
    private var adsWatched = 0
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 200
        }
        log("Service connected. Spy mode: $SPY_MODE")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return

        if (SPY_MODE) {
            val pkg = root.packageName?.toString() ?: "?"
            log("== Window: $pkg ==")
            dumpTree(root, 0)
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastActionTime < CYCLE_DELAY_MS) return

        val target = findNodeByText(root, TARGET_TEXT)
        if (target != null) {
            log("Clicking: text='${target.text}' id='${target.viewIdResourceName}'")
            target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            adsWatched++
            lastActionTime = now
            log("Ads watched this session: $adsWatched")
        }
    }

    override fun onInterrupt() {
        log("Service interrupted")
    }

    private fun findNodeByText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        val nodeText = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        if (nodeText.contains(text) || desc.contains(text)) {
            if (node.isClickable) return node
            var p = node.parent
            while (p != null) {
                if (p.isClickable) return p
                p = p.parent
            }
        }
        for (i in 0 until node.childCount) {
            val r = findNodeByText(node.getChild(i), text)
            if (r != null) return r
        }
        return null
    }

    private fun dumpTree(node: AccessibilityNodeInfo?, depth: Int) {
        if (node == null || depth > 12) return
        val indent = "  ".repeat(depth)
        val t = node.text?.toString() ?: ""
        val d = node.contentDescription?.toString() ?: ""
        val id = node.viewIdResourceName ?: ""
        if (t.isNotEmpty() || d.isNotEmpty() || id.isNotEmpty()) {
            log("$indent[$id] t='$t' d='$d' click=${node.isClickable}")
        }
        for (i in 0 until node.childCount) {
            dumpTree(node.getChild(i), depth + 1)
        }
    }

    private fun log(msg: String) {
        val line = "[${timeFmt.format(Date())}] $msg"
        Log.d(TAG, line)
        try {
            val f = File(filesDir, "derdan.log")
            f.appendText(line + "\n")
            // trim if too long
            val lines = f.readLines()
            if (lines.size > MAX_LOG_LINES) {
                f.writeText(lines.takeLast(MAX_LOG_LINES).joinToString("\n"))
            }
        } catch (_: Exception) {}
    }
}
