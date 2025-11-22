import android.graphics.PointF

data class GameBounds(val width: Float, val height: Float)

class ShipPhysics(
    var x: Float,
    var y: Float,
    private val bounds: GameBounds
) {
    // Velocity vector (how fast we are moving in X and Y)
    private var velocityX = 0f
    private var velocityY = 0f

    // TUNING VARIABLES --------------------------
    // How fast the ship accelerates when you push
    private val accelerationSpeed = 1.5f 
    
    // The "Drift Factor": 1.0 = infinite slide, 0.0 = instant stop.
    // 0.90 to 0.98 is usually the "Crystal Quest" sweet spot.
    private val friction = 0.95f 
    
    // Max speed to prevent flying through walls instantly
    private val maxSpeed = 25f
    // -------------------------------------------

    /**
     * Update the physics state.
     * @param inputX -1.0 (left) to 1.0 (right)
     * @param inputY -1.0 (up) to 1.0 (down)
     */
    fun update(inputX: Float, inputY: Float) {
        // 1. Apply Input (Acceleration)
        velocityX += inputX * accelerationSpeed
        velocityY += inputY * accelerationSpeed

        // 2. Apply Friction (The Drift)
        velocityX *= friction
        velocityY *= friction

        // 3. Clamp Speed (Optional, but good for stability)
        velocityX = velocityX.coerceIn(-maxSpeed, maxSpeed)
        velocityY = velocityY.coerceIn(-maxSpeed, maxSpeed)

        // 4. Update Position
        x += velocityX
        y += velocityY

        // 5. Handle Screen Edges (Bounce logic)
        checkBounds()
    }

    private fun checkBounds() {
        // Simple bounce mechanic: if we hit a wall, reverse velocity
        if (x < 0) {
            x = 0f
            velocityX = -velocityX * 0.5f // Lose some energy on bounce
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
    
    // Helper to get position for drawing
    fun getPosition(): PointF = PointF(x, y)
}
