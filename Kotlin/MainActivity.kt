class MainActivity : ComponentActivity() { // or AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide the status bar for full immersion
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN 
                                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
                                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                                            
        setContentView(GameView(this))
    }
}
