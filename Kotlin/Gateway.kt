// ... inside GameView ...

    // Gateway Dimensions
    private val gateWidth = 300f
    private val gateHeight = 60f
    
    // Gateway Visuals
    private val gatePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    // State tracking
    private var isGateOpen = false
    private var currentLevel = 1



// ... inside onDraw method ...

    // 1. Determine Gate State
    isGateOpen = crystals.isEmpty()

    // 2. Set Color (Red = Closed, Green = Open)
    gatePaint.color = if (isGateOpen) Color.GREEN else Color.RED
    
    // 3. Add a glowing effect if open
    if (isGateOpen) gatePaint.setShadowLayer(20f, 0f, 0f, Color.WHITE)
    else gatePaint.clearShadowLayer()

    // 4. Draw the Gate at the bottom center
    val gateLeft = (width - gateWidth) / 2f
    val gateTop = height - gateHeight
    val gateRight = gateLeft + gateWidth
    val gateBottom = height.toFloat()
    
    canvas.drawRect(gateLeft, gateTop, gateRight, gateBottom, gatePaint)
    


    // ... inside checkCollisions method ...

    // GATEWAY LOGIC
    // Simple box collision check
    val gateLeft = (width - gateWidth) / 2f
    val gateTop = height - gateHeight
    
    // Check if ship is visually inside the gate area
    // (We use ship Y + radius to check the bottom edge of the ship)
    val shipBottom = playerPos.y + 40f // 40f is ship radius
    val shipX = playerPos.x

    // Is the ship horizontally aligned with the gate?
    if (shipX > gateLeft && shipX < gateLeft + gateWidth) {
        
        // Is the ship vertically touching the gate?
        if (shipBottom >= gateTop) {
            if (isGateOpen) {
                // --- WIN STATE: NEXT LEVEL ---
                startNextLevel()
            } else {
                // --- CLOSED STATE: BOUNCE ---
                // Force ship out of the wall so it doesn't get stuck
                ship?.y = gateTop - 40f 
                // Reverse Y velocity (Bounce) - Assuming we add a generic 'bounceY' method 
                // or you can access velocityY directly if you make it public in ShipPhysics.
                // For now, let's just push it back up manually:
                // (Ideally, add a bounceVertical() method to ShipPhysics)
            }
        }
    }


        private fun startNextLevel() {
        currentLevel++
        score += 1000 // Level completion bonus
        
        // Reset Ship to center
        ship?.x = width / 2f
        ship?.y = height / 2f
        
        // Respawn Crystals (maybe add more for higher levels?)
        spawnCrystals(width, height)
        }
        
