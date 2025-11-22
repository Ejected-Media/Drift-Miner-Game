package main

import (
    "context"
    "fmt"
    "log"
    "net/http"
    "os"

    firebase "firebase.google.com/go/v4"
    "google.golang.org/api/option"
)

var firebaseApp *firebase.App

func main() {
    ctx := context.Background()

    // 1. Initialize Firebase
    // On App Engine, this often works automatically with default credentials.
    // For local dev, you might point to a serviceAccountKey.json
    conf := &firebase.Config{ProjectID: "drift-miner-game"}
    var err error
    
    // If local, use key file. If production, use Default Creds.
    if os.Getenv("APP_ENV") == "local" {
        opt := option.WithCredentialsFile("serviceAccountKey.json")
        firebaseApp, err = firebase.NewApp(ctx, conf, opt)
    } else {
        firebaseApp, err = firebase.NewApp(ctx, conf)
    }

    if err != nil {
        log.Fatalf("error initializing app: %v\n", err)
    }

    // 2. Setup Router (Standard Lib for now, or use Chi/Gin)
    http.HandleFunc("/api/v1/submit-score", handleSubmitScore)
    http.HandleFunc("/api/v1/leaderboard", handleGetLeaderboard)
    http.HandleFunc("/health", handleHealth)

    // 3. Start Server
    port := os.Getenv("PORT")
    if port == "" {
        port = "8080"
    }
    log.Printf("Drift Miner Backend listening on port %s", port)
    if err := http.ListenAndServe(":"+port, nil); err != nil {
        log.Fatal(err)
    }
}

// Simple Health Check
func handleHealth(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusOK)
    w.Write([]byte("Drift Miner Systems Operational"))
}

// Placeholders for now
func handleSubmitScore(w http.ResponseWriter, r *http.Request) {
    // TODO: Parse JSON, Validate Token, Save to DB
    fmt.Fprintf(w, "Score Received (Mock)")
}

func handleGetLeaderboard(w http.ResponseWriter, r *http.Request) {
    // TODO: Query Firestore
    fmt.Fprintf(w, "Leaderboard Data (Mock)")
}
