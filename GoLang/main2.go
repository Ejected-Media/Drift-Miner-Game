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
