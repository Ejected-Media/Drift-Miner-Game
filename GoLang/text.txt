/drift-miner-backend
├── app.yaml                # App Engine Configuration
├── main.go                 # Entry point & Router
├── go.mod                  # Dependencies
├── internal/
│   ├── db/
│   │   └── firestore.go    # Database logic
│   ├── handlers/
│   │   └── scores.go       # HTTP handlers for inputs
│   └── middleware/
│       └── auth.go         # Verifies Firebase ID Tokens
