# 🚀 SYSTÈME RAG COMPLET - DOCUMENTATION

## 📋 Vue d'ensemble

Système d'extraction et de Q&A basé sur les documents locaux (pas d'hallucinations).

### Fichiers extraits:
1. **Bulletin EYA JABER** (`bulletin.jpg`) → JSON
2. **Excel ISSTE** (`ISSTE_Conference_Scientifique_2025.xlsx`) → JSON  
3. **PDF IHEC RH** (`rapport_rh_ihec_2024-2025.pdf`) → JSON

---

## 🛠️ ARCHITECTURE

```
backend/
├── extract_all_documents.py      ← Extraction unified des 3 documents
├── app/
│   ├── main.py                  ← API FastAPI + endpoints RAG
│   └── services/
│       └── rag_service.py       ← Service RAG (chargement + Q&A)
└── test_rag_endpoints.py        ← Tests des endpoints
```

---

## 📦 PROCESSUS

### 1️⃣ EXTRACTION (une seule fois)

```bash
cd backend
python extract_all_documents.py
```

**Ce qu'il fait:**
- Extrait le bulletin avec GPT-4 Vision
- Extrait l'Excel avec pandas
- Extrait le PDF IHEC avec GPT-4 Vision (5 pages)
- Sauvegarde 3 fichiers JSON dans `app/services/extracted_data/`

**Fichiers créés:**
```
extracted_data/
├── bulletin/bulletin_eya_jaber_[timestamp]_extracted.json
├── isste/ISSTE_Conference_[timestamp]_extracted.json
└── ihec/rapport_rh_ihec_[timestamp]_extracted.json
```

### 2️⃣ DÉMARRAGE DE L'API

```bash
cd backend
python -m uvicorn app.main:app --reload --port 8000
```

**À la startup:**
- ✅ Charge automatiquement les 3 fichiers JSON
- ✅ Initialise le service RAG
- ✅ Prêt à répondre aux questions

### 3️⃣ TESTS (optionnel)

```bash
cd backend
python test_rag_endpoints.py
```

Teste tous les endpoints avec des questions d'exemple.

---

## 📡 ENDPOINTS

### 1. `GET /api/rag/documents`
Affiche les documents chargés

**Réponse:**
```json
{
  "documents": {
    "extracted_data/bulletin/bulletin_eya_jaber_...": {
      "type": "bulletin",
      "institution": "bulletin",
      "source": "bulletin.jpg"
    },
    ...
  },
  "total": 3
}
```

### 2. `POST /api/rag/ask` ⭐ (Principal)
Pose UNE question

**Paramètres:**
```
question (string): La question
```

**Exemples:**
```
POST /api/rag/ask?question=Quel%20est%20le%20taux%20d'absentéisme%20par%20département%20pour%20IHEC?

POST /api/rag/ask?question=Quelles%20sont%20les%20notes%20d'EYA%20JABER?

POST /api/rag/ask?question=Résume%20les%20données%20ISSTE
```

**Réponse:**
```json
{
  "question": "Quel est le taux d'absentéisme par département?",
  "answer": "Voici les taux d'absentéisme par département...",
  "sources": ["ihec/page_1", "ihec/page_2"],
  "success": true
}
```

### 3. `POST /api/rag/batch-ask`
Pose PLUSIEURS questions à la fois

**Paramètres:**
```json
[
  "Question 1?",
  "Question 2?",
  "Question 3?"
]
```

**Réponse:**
```json
{
  "success": true,
  "total_questions": 3,
  "results": [
    {"question": "...", "answer": "...", "sources": []},
    {"question": "...", "answer": "...", "sources": []}
  ]
}
```

### 4. `POST /api/rag/extract-all` (Optionnel)
Redéclenche l'extraction de tous les documents via l'API

---

## 🔍 EXAMPLES D'UTILISATION

### Via cURL

```bash
# Question simple
curl "http://localhost:8000/api/rag/ask?question=Quel%20est%20le%20taux%20d'absentéisme%20IHEC"

# Plusieurs questions
curl -X POST http://localhost:8000/api/rag/batch-ask \
  -H "Content-Type: application/json" \
  -d '["Combien d enseignants?", "Quelle est la moyenne d EYA?"]'
```

### Via Python

```python
import requests

# Question unique
response = requests.post(
    "http://localhost:8000/api/rag/ask",
    params={"question": "Quel est le taux d'absentéisme?"}
)
print(response.json())

# Plusieurs questions
response = requests.post(
    "http://localhost:8000/api/rag/batch-ask",
    json=["Question 1?", "Question 2?"]
)
print(response.json())
```

### Via Frontend (JavaScript)

```javascript
// Fetch une question
const response = await fetch('/api/rag/ask?question=Combien%20d%20enseignants%20IHEC');
const data = await response.json();
console.log(data.answer);
```

---

## 🔧 DÉTAILS TECHNIQUES

### Service RAG (`rag_service.py`)

**Classe:** `RAGService`

```python
# Initialisation
rag = RAGService()

# Charger les documents
rag.load_documents()  # Automatique au démarrage

# Extraire données pertinentes
relevant_data = rag.extract_relevant_data(question)

# Poser une question
result = rag.ask_question(question)
```

### Flux de réponse

```
Question
  ↓
extract_relevant_data()  ← Détecte institution + cherche dans tous les docs
  ↓
Données structurées (tables, métriques, textes)
  ↓
GPT-4 (avec instructions strictes: pas d'hallucination)
  ↓
Réponse + Sources
```

### Détection d'institution

Mots-clés:
- **IHEC**: "ihec", "absentéisme", "département", "rapport"
- **ISSTE**: "isste", "conférence", "conference scientifique"
- **Bulletin**: "eya", "jaber", "bulletin", "étudiant", "notes", "moyenne"

Si aucun mot-clé → cherche dans TOUS les documents

---

## ✅ CHECKLIST D'UTILISATION

- [ ] Lancer l'extraction: `python extract_all_documents.py`
- [ ] Vérifier les fichiers JSON créés: `app/services/extracted_data/`
- [ ] Démarrer l'API: `python -m uvicorn app.main:app --reload --port 8000`
- [ ] Vérifier la santé: `curl http://localhost:8000/health`
- [ ] Lister les documents: `curl http://localhost:8000/api/rag/documents`
- [ ] Poser une question: `curl "http://localhost:8000/api/rag/ask?question=..."`
- [ ] Tester les endpoints: `python test_rag_endpoints.py`

---

## 🐛 TROUBLESHOOTING

### Problem: "Aucun document trouvé"
**Solution:** Vérifiez que `extract_all_documents.py` a bien fonctionné
```bash
ls -la app/services/extracted_data/
```

### Problem: "OPENAI_API_KEY non configurée"
**Solution:** Créez/vérifiez `backend/.env`
```
OPENAI_API_KEY=sk-...
```

### Problem: "Model gpt-4-vision-preview deprecated"
**Solution:** Utilisé gpt-4o (déjà changé dans les scripts)

### Problem: Réponses incorrectes/hallucinations
**Solution:** Le RAG est conçu pour éviter ça. Les données viennent directement des JSONs.

---

## 📊 LIMITATIONS ACTUELLES

- Extraction limitée à 5 pages du PDF IHEC (pour économiser les tokens)
- Excel limité à 10 rows par table (mais tous les sheets inclus)
- Pas de recherche semantique (simple keyword matching)

---

## 🚀 PROCHAINES ÉTAPES

1. Augmenter le nombre de pages PDF extraites
2. Ajouter recherche semantique avec embeddings
3. Ajouter pagination pour les réponses longues
4. Intégrer au frontend React/Vue
5. Ajouter cache pour les questions fréquentes

---

**Version:** 1.0 | **Date:** 25 Avril 2026 | **Auteur:** IA Assistant
