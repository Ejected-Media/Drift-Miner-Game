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
