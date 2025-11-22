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
    
