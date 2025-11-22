import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class GameView(context: Context) : View(context) {

    // 1. GRAPHICS SETUP
    private val shipPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val joystickPaint = Paint().apply {
        color = Color.parseColor("#33FFFFFF") // Semi-transparent white
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    // 2. GAME STATE
    private var ship: ShipPhysics? = null
    private val shipRadius = 40f

    // Input Handling variables
    private var inputX = 0f
    private var inputY = 0f
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var isTouching = false

    // 3. INITIALIZATION
    // We wait for onSizeChanged so we know how big the screen is
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Initialize ship in the center of the screen
        val bounds = GameBounds(w.toFloat(), h.toFloat())
        ship = ShipPhysics(w / 2f, h / 2f, bounds)
    }

    // 4. THE GAME LOOP (onDraw)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Set background color (Deep Space Black)
        canvas.drawColor(Color.BLACK)

        val physics = ship ?: return

        // A. Update Physics
        physics.update(inputX, inputY)

        // B. Draw Ship
        val pos = physics.getPosition()
        canvas.drawCircle(pos.x, pos.y, shipRadius, shipPaint)

        // C. Draw Debug Joystick (Optional visual aid)
        if (isTouching) {
            canvas.drawCircle(touchStartX, touchStartY, 150f, joystickPaint)
            canvas.drawLine(touchStartX, touchStartY, 
                            touchStartX + (inputX * 150f), 
                            touchStartY + (inputY * 150f), 
                            shipPaint)
        }

        // D. Trigger next frame (Loop)
        invalidate() 
    }

    // 5. INPUT HANDLING (Virtual Joystick)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Anchor the joystick where the finger first touches
                touchStartX = event.x
                touchStartY = event.y
                isTouching = true
            }
            MotionEvent.ACTION_MOVE -> {
                // Calculate the distance from the anchor
                val dx = event.x - touchStartX
                val dy = event.y - touchStartY
                
                // Normalize logic (limit stick throw to 150 pixels)
                val maxThrow = 150f
                val distance = sqrt(dx * dx + dy * dy)
                
                // If within the circle, scale 0-1. If outside, cap at 1.
                val scale = if (distance > maxThrow) maxThrow else distance
                
                // Convert to -1.0 to 1.0 range for the physics engine
                if (distance > 0) {
                    inputX = (dx / distance) * (scale / maxThrow)
                    inputY = (dy / distance) * (scale / maxThrow)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Let go of the stick -> No acceleration (but drift continues!)
                inputX = 0f
                inputY = 0f
                isTouching = false
            }
        }
        return true
    }
}
