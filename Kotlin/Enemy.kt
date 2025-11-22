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


for (enemy in enemies) {
    // Update enemy logic (move towards player)
    ship?.getPosition()?.let { pos ->
        enemy.update(pos.x, pos.y)
    }
    // Draw enemy
    canvas.drawCircle(enemy.x, enemy.y, enemy.radius, enemy.paint)
}


    // ... inside checkCollisions ...
    for (enemy in enemies) {
        val dx = playerPos.x - enemy.x
        val dy = playerPos.y - enemy.y
        val distSq = dx*dx + dy*dy
        val radii = 40f + enemy.radius
        
        if (distSq < radii * radii) {
            // GAME OVER LOGIC
            // For now, just reset the level or subtract score
            score -= 500
            startNextLevel() // Restart
            return // Exit function to prevent errors
        }
    }
    
