#  RAG System – Architecture & Implementation

##  Table of Contents

1. [System Architecture](#system-architecture)
2. [Retrieval Strategy](#retrieval-strategy)
3. [Generation Strategy](#generation-strategy)
4. [Key Implementation Choices](#key-implementation-choices)
5. [Data Flow](#data-flow)
6. [Technical Stack](#technical-stack)

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      RAG SYSTEM ARCHITECTURE                     │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐         ┌──────────────────┐
│   React Frontend │◄───────►│  Spring Boot API │
│   (Port 5173)    │  HTTP   │   (Port 8080)    │
└──────────────────┘         └──────────────────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                 │                 │
                    ▼                 ▼                 ▼
           ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
           │   Ollama    │   │  ChromaDB   │   │  PDF Store  │
           │ (LLM + EMB) │   │  (Vectors)  │   │ (Documents) │
           │ Port 11434  │   │  Port 8000  │   │  Filesystem │
           └─────────────┘   └─────────────┘   └─────────────┘
```

---

##  Retrieval Strategy

### Overview

The retrieval strategy is based on **semantic similarity search** using vector embeddings stored in **ChromaDB**.

---

### Retrieval Pipeline

```
USER QUERY
    │
    ▼
[ Query Preprocessing ]
    │
    ▼
[ Query Embedding – nomic-embed-text ]
    │
    ▼
[ Similarity Search – Cosine Similarity ]
    │
    ▼
[ Top-K Selection + Threshold Filtering ]
    │
    ▼
[ Context Construction ]
```

---

### Retrieval Parameters

```properties
rag.max-results=5
rag.min-score=0.7
rag.chunk-size=500
rag.chunk-overlap=50
```

---

##  Generation Strategy

### Overview

The generation phase relies on **prompt engineering** and **retrieved context** to ensure grounded and accurate responses.

---

### Generation Pipeline

```
Retrieved Context + User Query
        │
        ▼
[ Prompt Engineering ]
        │
        ▼
[ Context Injection ]
        │
        ▼
[ LLM Generation – llama3.1 ]
        │
        ▼
[ Post-processing + Citations ]
```

---

##  Key Implementation Choices

### Embedding Model

- **Model**: nomic-embed-text
- **Dimensions**: 768
- **Execution**: Local

### LLM

- **Model**: llama3.1
- **Provider**: Ollama
- **Temperature**: 0.7
- **Max Tokens**: 1000

### Vector Database

- **ChromaDB**
- Local persistent vector store

---

##  Data Flow

### Phase 1 – Document Indexing

```
PDF Upload
   ↓
Text Extraction (PDFBox)
   ↓
Chunking
   ↓
Embedding Generation
   ↓
Vector Storage (ChromaDB)
```

### Phase 2 – Query Processing

```
User Question
   ↓
Query Embedding
   ↓
Vector Search
   ↓
Context Building
   ↓
Prompt Construction
   ↓
LLM Generation
   ↓
Final Answer
```

---

##  Technical Stack

| Layer | Technology |
|------|------------|
| Frontend | React + Vite |
| Backend | Spring Boot 3 |
| RAG Framework | LangChain4j |
| LLM | Ollama (llama3.1) |
| Embeddings | nomic-embed-text |
| Vector DB | ChromaDB |
| PDF Parsing | Apache PDFBox |
| Build Tool | Maven |

---

## Conclusion

This RAG system is fully local, configurable, and production-ready, suitable for academic projects and PFE implementations.
