import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

data class Crystal(
    var x: Float,
    var y: Float,
    val radius: Float = 25f // Slightly smaller than the ship
) {
    // Visuals for the crystal
    val paint = Paint().apply {
        color = Color.parseColor("#FF00FF") // Magenta/Purple style
        style = Paint.Style.FILL
        isAntiAlias = true
        // Add a glow effect (shadow)
        setShadowLayer(10f, 0f, 0f, Color.WHITE) 
    }
    
    // Rect for collision optimization (optional, but good practice)
    // For now, we stick to pure math
}
