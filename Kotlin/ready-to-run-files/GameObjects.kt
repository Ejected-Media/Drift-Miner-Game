package com.example.driftminer

import android.graphics.Color
import android.graphics.Paint
import kotlin.math.sqrt

// --- THE CRYSTAL (Objective) ---
data class Crystal(
    var x: Float,
    var y: Float
) {
    val radius = 25f
    val paint = Paint().apply {
        color = Color.parseColor("#FF00FF") // Magenta
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(10f, 0f, 0f, Color.WHITE)
    }
}

// --- THE ENEMY (The Threat) ---
data class Enemy(
    var x: Float,
    var y: Float
) {
    val radius = 35f
    private val speed = 4.0f // Constant chasing speed
    
    val paint = Paint().apply {
        color = Color.parseColor("#FF4500") // Orange Red
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun update(playerX: Float, playerY: Float) {
        val dx = playerX - x
        val dy = playerY - y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 0) {
            x += (dx / distance) * speed
            y += (dy / distance) * speed
        }
    }
}
