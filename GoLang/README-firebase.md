Here is the implementation for the database layer. This code handles the direct communication between your Go server and the Firestore NoSQL database.
Save this file as: internal/db/firestore.go
1. The Database Logic (firestore.go)
This module wraps the Firestore client so your main application code remains clean. It handles the two core operations: Writing a new high score and Reading the top scores.
package db

import (
    "context"
    "fmt"
    "log"
    "time"

    "cloud.google.com/go/firestore"
    firebase "firebase.google.com/go/v4"
    "google.golang.org/api/iterator"
)

// ScoreEntry mirrors the data structure we want in the database
type ScoreEntry struct {
    UserID    string    `json:"user_id" firestore:"user_id"`
    Username  string    `json:"username" firestore:"username"`
    Score     int       `json:"score" firestore:"score"`
    Level     int       `json:"level" firestore:"level"`
    Timestamp time.Time `json:"timestamp" firestore:"timestamp"`
    Platform  string    `json:"platform" firestore:"platform"`
}

// Repository holds the database client
type Repository struct {
    Client *firestore.Client
}

// NewRepository initializes the connection using the Firebase App from main.go
func NewRepository(ctx context.Context, app *firebase.App) (*Repository, error) {
    client, err := app.Firestore(ctx)
    if err != nil {
        return nil, fmt.Errorf("error getting firestore client: %v", err)
    }
    return &Repository{Client: client}, nil
}

// AddScore saves a score to the "scores" collection
func (r *Repository) AddScore(ctx context.Context, entry ScoreEntry) error {
    // We let Firestore auto-generate the Document ID
    _, _, err := r.Client.Collection("scores").Add(ctx, entry)
    if err != nil {
        return fmt.Errorf("failed to add score: %v", err)
    }
    return nil
}

// GetLeaderboard retrieves the top N high scores
func (r *Repository) GetLeaderboard(ctx context.Context, limit int) ([]ScoreEntry, error) {
    var scores []ScoreEntry

    // QUERY: Collection "scores" -> Order by "score" Descending -> Limit to N
    iter := r.Client.Collection("scores").
        OrderBy("score", firestore.Desc).
        Limit(limit).
        Documents(ctx)

    // Iterate through the results
    for {
        doc, err := iter.Next()
        if err == iterator.Done {
            break
        }
        if err != nil {
            return nil, err
        }

        var entry ScoreEntry
        // Map the document data to our Struct
        if err := doc.DataTo(&entry); err != nil {
            log.Printf("Error mapping data: %v", err)
            continue // Skip bad data, don't crash
        }
        scores = append(scores, entry)
    }

    return scores, nil
}

// Close cleans up the client connection
func (r *Repository) Close() {
    r.Client.Close()
}

2. Updating main.go to use it
Now we need to update your main.go to actually use this new db package.
We need to:
 * Initialize the Repository.
 * Pass the Repository to the HTTP handlers.
Here is the updated Handler Logic (you can put this in main.go or a separate handlers.go).
// ... imports including "your-project/internal/db"

// Wrapper to inject the DB dependency into the handler
func handleSubmitScore(repo *db.Repository) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        if r.Method != http.MethodPost {
            http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
            return
        }

        // 1. Parse JSON Body
        var entry db.ScoreEntry
        if err := json.NewDecoder(r.Body).Decode(&entry); err != nil {
            http.Error(w, "Invalid JSON", http.StatusBadRequest)
            return
        }

        // 2. Validate (Basic Example)
        if entry.Score < 0 || entry.Username == "" {
            http.Error(w, "Invalid data", http.StatusBadRequest)
            return
        }
        
        // Add Server-Side Timestamp
        entry.Timestamp = time.Now()

        // 3. Save to DB
        ctx := r.Context()
        if err := repo.AddScore(ctx, entry); err != nil {
            log.Printf("Database error: %v", err)
            http.Error(w, "Internal Server Error", http.StatusInternalServerError)
            return
        }

        w.WriteHeader(http.StatusCreated)
        w.Write([]byte("Score Submitted Successfully"))
    }
}

func handleGetLeaderboard(repo *db.Repository) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        // 1. Fetch Top 50
        scores, err := repo.GetLeaderboard(r.Context(), 50)
        if err != nil {
            log.Printf("Database error: %v", err)
            http.Error(w, "Failed to fetch leaderboard", http.StatusInternalServerError)
            return
        }

        // 2. Return as JSON
        w.Header().Set("Content-Type", "application/json")
        json.NewEncoder(w).Encode(scores)
    }
}

3. Critical: The Firestore Index
When you run OrderBy("score", firestore.Desc), Firestore requires an Index.
 * The first time you run this code, it will likely fail with an error in the logs that looks like:
   > "The query requires an index. You can create it here: https://console.firebase.google.com/project/..."
   > 
 * Click that link. It will automatically create the index for you in the Firebase Console. Do not try to manually configure it unless you have to; the link is the safest way.
Summary of Backend Status
 * Server: Go HTTP server running.
 * Database: Firestore connected.
 * Writes: Validated JSON is saved to the scores collection.
 * Reads: Top 50 scores are retrieved via a descending query.
Next Step
We have the Game Client (Android/Kotlin) and the Server API (Go). The final piece is connecting them.
Would you like to design the Android Network Client (using Retrofit) to send the score to this API?
