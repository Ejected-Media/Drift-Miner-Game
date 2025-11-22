package main

import "time"

// ScoreEntry represents a single game session record
type ScoreEntry struct {
    UserID    string    `json:"user_id" firestore:"user_id"`       // From Firebase Auth
    Username  string    `json:"username" firestore:"username"`     // Display Name
    Score     int       `json:"score" firestore:"score"`           // The raw score
    Level     int       `json:"level" firestore:"level"`           // Level reached (validation metric)
    Timestamp time.Time `json:"timestamp" firestore:"timestamp"`   // When it happened
    Platform  string    `json:"platform" firestore:"platform"`     // "android", "ios", etc.
}
