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
