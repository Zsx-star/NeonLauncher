package com.neon.launcher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : Activity() {
    private val mc = "com.mojang.minecraftpe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0E1220"))
            setPadding(64, 64, 64, 64)
        }
        root.addView(TextView(this).apply {
            text = "NEON LAUNCHER"
            textSize = 30f
            setTextColor(Color.parseColor("#00E5FF"))
            gravity = Gravity.CENTER
        })
        root.addView(neonButton("▶  Запустить Minecraft", "#00E5FF") { launchMc() })
        root.addView(neonButton("🎨  Установить Neon Glass UI", "#FF00AA") { installPack() })
        setContentView(root)
    }

    private fun neonButton(label: String, color: String, onClick: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 18f
        setTextColor(Color.parseColor(color))
        background = GradientDrawable().apply {
            setColor(Color.parseColor("#1A2038"))
            setStroke(4, Color.parseColor(color))
            cornerRadius = 40f
        }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 180
        ).apply { topMargin = 48 }
        setOnClickListener { onClick() }
    }

    private fun launchMc() {
        val i = packageManager.getLaunchIntentForPackage(mc)
        if (i == null) toast("Minecraft не установлен") else startActivity(i)
    }

    private fun installPack() {
        try {
            val f = File(cacheDir, "NeonGlassUI.mcpack")
            assets.open("NeonGlassUI.mcpack").use { inp -> f.outputStream().use { inp.copyTo(it) } }
            val uri = FileProvider.getUriForFile(this, "com.neon.launcher.files", f)
            val i = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/octet-stream")
                setPackage(mc)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(i)
        } catch (e: Exception) {
            toast("Не удалось открыть Minecraft")
        }
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
