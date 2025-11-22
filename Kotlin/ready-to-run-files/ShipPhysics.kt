package com.example.driftminer

import android.graphics.PointF

data class GameBounds(val width: Float, val height: Float)

class ShipPhysics(
    var x: Float,
    var y: Float,
    private val bounds: GameBounds
) {
    private var velocityX = 0f
    private var velocityY = 0f

    // PHYSICS TUNING
    private val accelerationSpeed = 1.5f
    private val friction = 0.95f // The "Drift" factor
    private val maxSpeed = 25f

    fun update(inputX: Float, inputY: Float) {
        // 1. Acceleration
        velocityX += inputX * accelerationSpeed
        velocityY += inputY * accelerationSpeed

        // 2. Friction
        velocityX *= friction
        velocityY *= friction

        // 3. Clamp Speed
        velocityX = velocityX.coerceIn(-maxSpeed, maxSpeed)
        velocityY = velocityY.coerceIn(-maxSpeed, maxSpeed)

        // 4. Position
        x += velocityX
        y += velocityY

        // 5. Screen Bounce
        checkBounds()
    }

    private fun checkBounds() {
        if (x < 0) {
            x = 0f
            velocityX = -velocityX * 0.5f
        } else if (x > bounds.width) {
            x = bounds.width
            velocityX = -velocityX * 0.5f
        }

        if (y < 0) {
            y = 0f
            velocityY = -velocityY * 0.5f
        } else if (y > bounds.height) {
            y = bounds.height
            velocityY = -velocityY * 0.5f
        }
    }

    // Helper to bounce off the Gateway (Horizontal wall)
    fun bounceVertical() {
        velocityY = -velocityY * 0.8f // Bounce with slight energy loss
    }

    fun getPosition(): PointF = PointF(x, y)
}
