package com.scrollguard.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants

/**
 * Foreground service that displays an impassable full-screen blocking overlay
 * when an app exceeds its daily screen-time or reel limit.
 */
class AppBlockerService : Service() {

    private var windowManager: WindowManager? = null
    private var blockView: View? = null

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_IS_STRICT = "extra_is_strict"
        private const val NOTIFICATION_ID = 3001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: "App"
        val isStrict = intent?.getBooleanExtra(EXTRA_IS_STRICT, false) ?: false

        showBlockOverlay(appName, isStrict)
        return START_NOT_STICKY
    }

    @SuppressLint("SetTextI18n")
    private fun showBlockOverlay(appName: String, isStrict: Boolean) {
        if (blockView != null) {
            removeBlockOverlay()
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#EE121212")) // Dark scrim background
            setPadding(64, 64, 64, 64)
        }

        val iconText = TextView(this).apply {
            text = "⏳"
            textSize = 64f
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Time's up for $appName!"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 16)
        }

        val subtitle = TextView(this).apply {
            text = "You've reached your daily limit. Take a break and focus on what matters."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }

        val closeButton = Button(this).apply {
            text = "Close $appName"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#6750A4"))
                cornerRadius = 24f
            }
            setPadding(32, 16, 32, 16)
            setOnClickListener {
                goToHomeScreen()
                stopSelf()
            }
        }

        rootLayout.addView(iconText)
        rootLayout.addView(title)
        rootLayout.addView(subtitle)
        rootLayout.addView(closeButton)

        if (!isStrict) {
            val extendButton = Button(this).apply {
                text = "5 More Minutes"
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#333333"))
                    cornerRadius = 24f
                    setStroke(2, Color.DKGRAY)
                }
                setPadding(32, 16, 32, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 24
                }
                setOnClickListener {
                    removeBlockOverlay()
                    stopSelf()
                }
            }
            rootLayout.addView(extendButton)
        }

        blockView = rootLayout
        try {
            windowManager?.addView(blockView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }

    private fun removeBlockOverlay() {
        if (blockView != null) {
            try {
                windowManager?.removeView(blockView)
            } catch (e: Exception) {
                // View already detached
            }
            blockView = null
        }
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_BLOCKER)
            .setContentTitle("App Limit Reached")
            .setContentText("ScrollGuard has blocked an app exceeding its daily limit")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        removeBlockOverlay()
        super.onDestroy()
    }
}
