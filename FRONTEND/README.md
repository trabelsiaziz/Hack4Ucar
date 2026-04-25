# 🚀 HACK4UCAR - Système RAG pour Q&A sur Documents

Système complet d'extraction et de questions/réponses basé sur documents locaux (PDF, Excel, Images).

## 🎯 Fonctionnalités

- ✅ **Extraction intelligente** avec GPT-4 Vision (images, tableaux, texte)
- ✅ **RAG sans hallucinations** - Données directes du JSON
- ✅ **Multi-documents** - IHEC, ISSTE, Bulletin étudiants
- ✅ **API REST minimaliste** - 6 endpoints essentiels
- ✅ **Swagger UI** - Documentation interactive

## 📦 Structure

```
backend/
├── app/
│   ├── main.py                 # API FastAPI
│   └── services/
│       ├── rag_service.py      # Service RAG
│       └── extracted_data/     # Données JSON extraites
├── extract_all_documents.py    # Extraction unified
├── test_rag_endpoints.py       # Tests
├── setup_rag.py               # Setup guidé
└── requirements.txt
```

## 🚀 Démarrage rapide

### Installation
```bash
cd backend
pip install -r requirements.txt
```

### Extraction des documents
```bash
python extract_all_documents.py
```

### Lancer l'API
```bash
python -m uvicorn app.main:app --reload --port 8000
```

### Tester
- **Swagger UI**: http://localhost:8000/docs
- **Endpoint RAG**: `POST /api/rag/ask?question=...`

## 📡 Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/health` | Santé de l'API |
| GET | `/api/rag/documents` | Lister les documents |
| **POST** | **`/api/rag/ask`** | **Poser une question** |
| POST | `/api/rag/batch-ask` | Plusieurs questions |
| POST | `/api/rag/extract-all` | Redéclencher extraction |

## 💡 Exemples

### Question simple
```bash
curl -X POST "http://localhost:8000/api/rag/ask?question=Quel%20est%20le%20taux%20d'absentéisme%20IHEC"
```

### Python
```python
import requests
response = requests.post(
    "http://localhost:8000/api/rag/ask",
    params={"question": "Combien d'enseignants à Marketing?"}
)
print(response.json())
```

## 📚 Documentation

- [RAG_DOCUMENTATION.md](backend/RAG_DOCUMENTATION.md) - Documentation complète
- [STRUCTURE_FINALE.md](backend/STRUCTURE_FINALE.md) - Structure du projet

## 🔧 Configuration

Créez `backend/.env`:
```
OPENAI_API_KEY=sk-...
```

## ✅ Tests

```bash
cd backend
python test_rag_endpoints.py
```

## 📄 License

MIT
