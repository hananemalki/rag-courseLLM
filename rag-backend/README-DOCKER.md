#  Docker Setup Guide

Complete guide for running the RAG Backend with Docker.

## Quick Start (Recommended)

### 1. Clone the Repository


### 2. Run Setup Script

```bash
chmod +x setup.sh
./setup.sh
```

That's it! The script will:
-  Check dependencies
-  Start all Docker services
-  Download Ollama models
-  Wait for services to be ready
-  Display access information

## Manual Setup

### Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

### Steps

#### 1. Start Services

```bash
docker-compose up -d
```

#### 2. Download Ollama Models

```bash
# Wait for Ollama to start (about 30 seconds)
docker exec rag-ollama ollama pull llama3.2
docker exec rag-ollama ollama pull nomic-embed-text
```

#### 3. Verify Services

```bash
docker-compose ps

# Check 
curl http://localhost:8080/api/chat/health
```

## Services Overview

| Service | Port | Purpose |
|---------|------|---------|
| RAG Backend | 8080 | Main API |
| ChromaDB | 8000 | Vector Database |
| Ollama | 11434 | LLM Service |

## Docker Commands Cheatsheet

### Basic Operations

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart a service
docker-compose restart app

# View logs
docker-compose logs -f app

# View all logs
docker-compose logs -f
```

### Maintenance

```bash
# Rebuild application image
docker-compose build app

# Rebuild without cache
docker-compose build --no-cache app

# Remove all volumes ( deletes all data)
docker-compose down -v

# Check disk usage
docker system df
```

### Debugging

```bash
# Access application container
docker exec -it rag-backend sh

# Access Ollama container
docker exec -it rag-ollama bash

# Check ChromaDB status
curl http://localhost:8000/api/v1/heartbeat

# List Ollama models
docker exec rag-ollama ollama list
```

## Environment Variables

You can customize the setup by creating a `.env` file:

```bash
# .env file
OLLAMA_BASE_URL=http://ollama:11434
CHROMA_DB_URL=http://chromadb:8000
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

## Volumes

Data is persisted in Docker volumes:

- `chromadb-data`: Vector database storage
- `ollama-data`: LLM models storage
- `./documents`: PDF documents (bind mount)

## Troubleshooting

### Services won't start

```bash
# Check Docker daemon
docker info

# Check logs for errors
docker-compose logs
```

