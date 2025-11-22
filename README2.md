# Drift Miner

**Drift Miner** is a modern, high-speed arcade shooter for Android, serving as a spiritual successor to the classic MacOS game *Crystal Quest*.

Developed by **Ejected Media**.

## 🎮 Core Gameplay
* **The Drift:** Physics-based movement with inertia. The ship doesn't stop instantly; it drifts, requiring precise counter-thrusting.
* **The Miner:** Collect all magenta crystals on the screen to open the Gateway.
* **The Threat:** Avoid "Nasties" (Chaser enemies) and Mines while clearing the board.
* **The Loop:** Clear the level -> Enter the Gate -> Repeat (Infinite scaling difficulty).

## 🛠️ Tech Stack
* **Language:** Kotlin
* **Platform:** Android (Native)
* **Rendering:** Custom `View` / `Canvas` (High-performance 2D drawing)
* **Architecture:** Zero-dependency game loop embedded in a standard Android Activity.

## 📂 Project Structure
* `ShipPhysics.kt`: Handles velocity, friction (damping), and screen boundary bounce logic.
* `GameView.kt`: The main game loop, rendering engine, and input handler (Virtual Joystick).
* `GameObjects.kt`: Data classes for Crystals and Enemies.

## 🚀 Roadmap
- [x] Basic Physics Engine (Drift)
- [x] Virtual Joystick Input
- [x] Crystal Collection Logic
- [x] Enemy AI (Chaser)
- [ ] Particle Effects (Explosions)
- [ ] Sound Engine
- [ ] High Score System (GoLang + Firebase Backend)
- [ ] 
