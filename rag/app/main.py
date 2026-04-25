"""
main.py — API FastAPI pour le système RAG
Version minimaliste: uniquement les endpoints nécessaires
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from typing import List
import os
import sys
from dotenv import load_dotenv

load_dotenv()

sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

from app.db.database import init_db

# ============================================================================
# Configuration FastAPI
# ============================================================================

app = FastAPI(
    title="HACK4UCAR RAG API",
    version="2.0",
    description="🤖 Système RAG pour questions/réponses sur documents extraits"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # À restreindre en production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

# ============================================================================
# Startup
# ============================================================================

@app.on_event("startup")
def startup():
    """Initialisation au démarrage"""
    init_db()
    
    # Initialiser le service RAG
    from app.services.rag_service import get_rag_service
    print("📚 Initialisation du service RAG...")
    rag = get_rag_service()
    
    print("✅ API RAG démarrée")
    print("=" * 70)

# ============================================================================
# Endpoints de Santé
# ============================================================================

@app.get("/", tags=["Health"])
def root():
    """Endpoint racine"""
    return {
        "message": "HACK4UCAR RAG API v2.0 🚀",
        "status": "running",
        "docs": "http://localhost:8000/docs"
    }


@app.get("/health", tags=["Health"])
def health():
    """Vérifier la santé de l'API"""
    return {"status": "ok", "service": "rag"}

# ============================================================================
# Endpoints RAG
# ============================================================================

@app.get("/api/rag/documents", tags=["RAG"])
def get_rag_documents():
    """
    📄 Liste tous les documents chargés dans le système RAG
    
    Retourne la liste des documents extraits avec leurs métadonnées.
    """
    from app.services.rag_service import get_rag_service
    rag = get_rag_service()
    
    docs_info = {}
    for doc_path, doc_data in rag.documents.items():
        metadata = doc_data.get("metadata", {})
        docs_info[doc_path] = {
            "type": metadata.get("type", "unknown"),
            "institution": metadata.get("institution_id", "unknown"),
            "source": metadata.get("source", "unknown")
        }
    
    return {
        "success": True,
        "documents": docs_info,
        "total": len(docs_info)
    }


@app.post("/api/rag/ask", tags=["RAG"])
def ask_rag_question(question: str):
    """
    ❓ Pose une question sur les documents extraits
    
    Le système:
    1. Détecte automatiquement l'institution mentionnée
    2. Extrait les données pertinentes
    3. Envoie à GPT-4 avec instructions strictes
    4. Retourne une réponse basée sur les vraies données
    
    **Exemples de questions:**
    - "Quel est le taux d'absentéisme par département pour IHEC?"
    - "Combien d'enseignants à Marketing?"
    - "Quelles sont les notes d'EYA JABER?"
    - "Résume les données ISSTE"
    """
    if not question or len(question.strip()) == 0:
        return {
            "success": False,
            "error": "La question ne peut pas être vide",
            "question": question
        }
    
    try:
        from app.services.rag_service import get_rag_service
        rag = get_rag_service()
        result = rag.ask_question(question)
        return result
    except Exception as e:
        return {
            "success": False,
            "error": f"Erreur: {str(e)}",
            "question": question
        }


@app.post("/api/rag/batch-ask", tags=["RAG"])
def ask_batch_questions(questions: List[str]):
    """
    ❓❓❓ Pose plusieurs questions à la fois
    
    Utile pour traiter plusieurs questions en une seule requête.
    """
    
    if not questions or len(questions) == 0:
        return {
            "success": False,
            "error": "Aucune question fournie",
            "results": []
        }
    
    try:
        from app.services.rag_service import get_rag_service
        rag = get_rag_service()
        
        results = []
        for q in questions:
            result = rag.ask_question(q)
            results.append(result)
        
        return {
            "success": True,
            "total_questions": len(questions),
            "results": results
        }
    except Exception as e:
        return {
            "success": False,
            "error": f"Erreur: {str(e)}",
            "results": []
        }


@app.post("/api/rag/extract-all", tags=["RAG"])
def extract_all_documents():
    """
    🔄 Déclenche l'extraction de tous les documents
    
    Lance le script d'extraction unified.
    Attention: Cela prendra plusieurs minutes (appels GPT-4).
    """
    import subprocess
    
    try:
        result = subprocess.run(
            ["python", "extract_all_documents.py"],
            cwd=os.path.dirname(os.path.dirname(__file__)),
            capture_output=True,
            text=True,
            timeout=600
        )
        
        return {
            "success": result.returncode == 0,
            "message": "Extraction lancée",
            "output": result.stdout[:500] if result.stdout else "",
            "error": result.stderr[:500] if result.returncode != 0 else None
        }
    except Exception as e:
        return {
            "success": False,
            "error": f"Erreur: {str(e)}"
        }


# ============================================================================
# 404 Handler
# ============================================================================

@app.get("/{path_name:path}", tags=["Other"])
def not_found(path_name: str):
    """Endpoint par défaut pour les routes non trouvées"""
    return {
        "error": f"Route '/{path_name}' non trouvée",
        "available_endpoints": [
            "GET /",
            "GET /health",
            "GET /api/rag/documents",
            "POST /api/rag/ask",
            "POST /api/rag/batch-ask",
            "POST /api/rag/extract-all",
            "GET /docs (Swagger)"
        ]
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
