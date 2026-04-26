# Ucar Platform — System Architecture

```mermaid
graph TD
    Browser["Browser"]

    subgraph docker["Docker Network"]
        FE["frontend :3000\nNext.js — node:20-alpine"]
        BE["backend :8080\nSpring Boot — eclipse-temurin:21-jre-alpine"]
        RAG["rag :8000\nFastAPI — python:3.11-alpine"]
        OL["ollama :11434\nLLM hub — nginx:alpine stub"]
        PG[("postgres :5432\npostgres:16-alpine")]
    end

    Browser -->|"HTTP"| FE
    FE -->|"REST /api/dashboard/*\n/api/students  /api/teachers"| BE
    FE -->|"POST /api/rag/ask"| RAG

    BE -->|"JDBC"| PG
    BE -->|"LLM calls"| OL

    RAG -->|"embed / generate"| OL
```

## Startup order

```
postgres → healthy
ollama   → healthy
              └─ backend  → healthy
              └─ rag      → healthy
                                └─ frontend
```

## Port map

| Service  | Host port | Internal |
|----------|-----------|----------|
| frontend | 3000      | 3000     |
| backend  | 8080      | 8080     |
| rag      | 8000      | 8000     |
| ollama   | 11434     | 11434    |
| postgres | 5432      | 5432     |
