package com.example.driftminer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    // --- PAINTS ---
    private val shipPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val joystickPaint = Paint().apply {
        color = Color.parseColor("#33FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val gatePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val scorePaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        isAntiAlias = true
    }

    // --- GAME OBJECTS ---
    private var ship: ShipPhysics? = null
    private val shipRadius = 40f
    private val crystals = mutableListOf<Crystal>()
    private val enemies = mutableListOf<Enemy>()
    
    // --- STATE ---
    private var score = 0
    private var currentLevel = 1
    
    // Gateway Config
    private val gateWidth = 300f
    private val gateHeight = 60f
    private var isGateOpen = false

    // Input Config
    private var inputX = 0f
    private var inputY = 0f
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var isTouching = false

    // --- INITIALIZATION ---
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val bounds = GameBounds(w.toFloat(), h.toFloat())
        ship = ShipPhysics(w / 2f, h / 2f, bounds)
        
        startLevel(1)
    }

    private fun startLevel(level: Int) {
        currentLevel = level
        val w = width
        val h = height
        if (w == 0 || h == 0) return

        crystals.clear()
        enemies.clear()

        // Spawn Crystals
        repeat(5 + level) {
            val rX = Random.nextFloat() * (w - 200) + 100
            val rY = Random.nextFloat() * (h - 200) + 100
            crystals.add(Crystal(rX, rY))
        }
        
        // Spawn Enemies (More enemies on higher levels)
        repeat(level) {
             enemies.add(Enemy(0f, 0f)) // Spawn at top left
        }
    }

    // --- MAIN LOOP ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        val player = ship ?: return

        // 1. UPDATE PHYSICS & AI
        player.update(inputX, inputY)
        for (enemy in enemies) enemy.update(player.x, player.y)

        // 2. CHECK COLLISIONS
        checkCollisions()

        // 3. DRAW GATEWAY
        isGateOpen = crystals.isEmpty()
        gatePaint.color = if (isGateOpen) Color.GREEN else Color.RED
        if (isGateOpen) gatePaint.setShadowLayer(20f, 0f, 0f, Color.WHITE) else gatePaint.clearShadowLayer()
        
        val gateLeft = (width - gateWidth) / 2f
        val gateTop = height - gateHeight
        canvas.drawRect(gateLeft, gateTop, gateLeft + gateWidth, height.toFloat(), gatePaint)

        // 4. DRAW OBJECTS
        for (c in crystals) canvas.drawCircle(c.x, c.y, c.radius, c.paint)
        for (e in enemies) canvas.drawCircle(e.x, e.y, e.radius, e.paint)
        
        val pos = player.getPosition()
        canvas.drawCircle(pos.x, pos.y, shipRadius, shipPaint)

        // 5. DRAW UI
        canvas.drawText("Lvl: $currentLevel  Score: $score", 50f, 100f, scorePaint)
        if (isTouching) {
            canvas.drawCircle(touchStartX, touchStartY, 150f, joystickPaint)
            canvas.drawLine(touchStartX, touchStartY, touchStartX + (inputX*150f), touchStartY + (inputY*150f), shipPaint)
        }

        invalidate() // Request next frame
    }

    // --- COLLISION LOGIC ---
    private fun checkCollisions() {
        val player = ship ?: return
        val playerPos = player.getPosition()

        // A. Crystal Collection
        val crystalIter = crystals.iterator()
        while (crystalIter.hasNext()) {
            val c = crystalIter.next()
            if (distSq(playerPos.x, playerPos.y, c.x, c.y) < pow2(shipRadius + c.radius)) {
                crystalIter.remove()
                score += 100
            }
        }

        // B. Enemy Collision (Game Over check)
        for (e in enemies) {
            if (distSq(playerPos.x, playerPos.y, e.x, e.y) < pow2(shipRadius + e.radius)) {
                // For prototype: Just reset level and penalize score
                score = (score - 500).coerceAtLeast(0)
                startLevel(currentLevel)
                return
            }
        }

        // C. Gateway Interaction
        val gateLeft = (width - gateWidth) / 2f
        val gateTop = height - gateHeight
        
        // Simple bounding box check for the gate area
        if (playerPos.x > gateLeft && playerPos.x < gateLeft + gateWidth) {
            if (playerPos.y + shipRadius >= gateTop) {
                if (isGateOpen) {
                    // NEXT LEVEL
                    score += 1000
                    ship?.x = width / 2f
                    ship?.y = height / 2f
                    startLevel(currentLevel + 1)
                } else {
                    // CLOSED - BOUNCE
                    ship?.y = gateTop - shipRadius
                    ship?.bounceVertical()
                }
            }
        }
    }
    
    // Helpers
    private fun distSq(x1: Float, y1: Float, x2: Float, y2: Float) = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)
    private fun pow2(f: Float) = f * f

    // --- INPUT HANDLING ---
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                isTouching = true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - touchStartX
                val dy = event.y - touchStartY
                val maxThrow = 150f
                val dist = sqrt(dx*dx + dy*dy)
                val scale = if (dist > maxThrow) maxThrow else dist
                if (dist > 0) {
                    inputX = (dx/dist) * (scale/maxThrow)
                    inputY = (dy/dist) * (scale/maxThrow)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                inputX = 0f
                inputY = 0f
                isTouching = false
            }
        }
        return true
    }
}
