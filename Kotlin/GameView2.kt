// ... inside GameView class ...

    // 1. NEW VARIABLES
    private val crystals = mutableListOf<Crystal>()
    private val scorePaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        isAntiAlias = true
    }
    private var score = 0

    // 2. SPAWNING LOGIC (Call this inside onSizeChanged)
    private fun spawnCrystals(width: Int, height: Int) {
        crystals.clear()
        // Spawn 10 crystals randomly
        repeat(10) {
            // Keep them away from the absolute edges (padding of 100)
            val rX = Random.nextFloat() * (width - 200) + 100
            val rY = Random.nextFloat() * (height - 200) + 100
            crystals.add(Crystal(rX, rY))
        }
    }

    // Update onSizeChanged to call the spawner
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val bounds = GameBounds(w.toFloat(), h.toFloat())
        ship = ShipPhysics(w / 2f, h / 2f, bounds)
        
        // NEW: Create the level
        spawnCrystals(w, h)
    }

    // 3. COLLISION LOGIC (Helper function)
    private fun checkCollisions() {
        val player = ship ?: return
        val playerPos = player.getPosition()
        
        // Use an iterator to safely remove items while looping
        val iterator = crystals.iterator()
        while (iterator.hasNext()) {
            val crystal = iterator.next()
            
            // A. Calculate Distance Squared
            val dx = playerPos.x - crystal.x
            val dy = playerPos.y - crystal.y
            val distSq = dx * dx + dy * dy
            
            // B. Calculate Combined Radius Squared
            val combinedRadius = 40f + crystal.radius // 40f is ship radius
            val minDistSq = combinedRadius * combinedRadius
            
            // C. Check Intersection
            if (distSq < minDistSq) {
                // HIT!
                iterator.remove() // Remove crystal from world
                score += 100      // Increase score
                
                // Optional: Play sound here
            }
        }
        
        // D. Check for Level Complete
        if (crystals.isEmpty()) {
            // TODO: Open the Gateway logic
        }
    }

    // 4. UPDATE ON_DRAW
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // Update Physics
        ship?.update(inputX, inputY)
        
        // NEW: Check Collisions
        checkCollisions()

        // NEW: Draw Crystals
        for (crystal in crystals) {
            canvas.drawCircle(crystal.x, crystal.y, crystal.radius, crystal.paint)
        }

        // Draw Ship (Existing code)
        ship?.let { physics ->
            val pos = physics.getPosition()
            canvas.drawCircle(pos.x, pos.y, 40f, shipPaint)
        }

        // NEW: Draw Score
        canvas.drawText("Score: $score", 50f, 100f, scorePaint)

        // Draw Joystick (Existing code...)
        if (isTouching) {
             // ... existing joystick code ...
        }

        invalidate()
    }
    
