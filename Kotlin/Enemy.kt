data class Enemy(
    var x: Float, 
    var y: Float
) {
    val radius = 35f
    val speed = 4.0f // Slower than player max speed
    
    val paint = Paint().apply {
        color = Color.parseColor("#FF4500") // Orange/Red
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun update(playerX: Float, playerY: Float) {
        // 1. Get distance to player
        val dx = playerX - x
        val dy = playerY - y
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

        // 2. Normalize and Move (Standard "Homing" math)
        if (distance > 0) {
            x += (dx / distance) * speed
            y += (dy / distance) * speed
        }
    }
}
