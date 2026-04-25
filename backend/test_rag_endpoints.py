"""
test_rag_endpoints.py — Test des endpoints RAG
"""

import requests
import json
import time
import subprocess
import sys
from pathlib import Path

# Configuration
BASE_URL = "http://localhost:8000"
TIMEOUT = 30

def print_section(title):
    print("\n" + "="*70)
    print(f"🧪 {title}")
    print("="*70)

def print_result(response, title="Réponse"):
    print(f"\n📌 {title}:")
    if response.status_code == 200:
        data = response.json()
        print(json.dumps(data, indent=2, ensure_ascii=False))
    else:
        print(f"❌ Status: {response.status_code}")
        print(response.text)

def test_health():
    """Test santé de l'API"""
    print_section("Test Santé API")
    
    try:
        response = requests.get(f"{BASE_URL}/health", timeout=TIMEOUT)
        print_result(response, "Santé")
        return response.status_code == 200
    except Exception as e:
        print(f"❌ Erreur: {e}")
        return False

def test_rag_documents():
    """Teste le chargement des documents"""
    print_section("Documents RAG Chargés")
    
    try:
        response = requests.get(f"{BASE_URL}/api/rag/documents", timeout=TIMEOUT)
        print_result(response, "Documents")
        return response.status_code == 200
    except Exception as e:
        print(f"❌ Erreur: {e}")
        return False

def test_ask_question(question):
    """Teste une question"""
    print_section(f"Question: {question}")
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/rag/ask",
            params={"question": question},
            timeout=TIMEOUT
        )
        print_result(response, "Réponse")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success", True):
                print(f"\n✅ Source: {', '.join(data.get('sources', []))}")
                return True
        return False
    except Exception as e:
        print(f"❌ Erreur: {e}")
        return False

def test_batch_questions(questions):
    """Teste plusieurs questions"""
    print_section("Plusieurs Questions")
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/rag/batch-ask",
            json=questions,
            timeout=TIMEOUT
        )
        print_result(response, "Résultats")
        return response.status_code == 200
    except Exception as e:
        print(f"❌ Erreur: {e}")
        return False

def main():
    print("\n🚀 TEST DES ENDPOINTS RAG")
    print("="*70)
    
    # Vérifier que l'API est en cours d'exécution
    print("\n⏳ Vérification de l'API...")
    
    try:
        response = requests.get(f"{BASE_URL}/health", timeout=2)
        print("✅ API disponible")
    except:
        print("❌ API non disponible sur localhost:8000")
        print("\nLancez d'abord le serveur:")
        print("  cd backend")
        print("  python -m uvicorn app.main:app --reload --port 8000")
        sys.exit(1)
    
    # Tests
    results = {
        "santé": test_health(),
        "documents": test_rag_documents(),
    }
    
    # Questions de test
    test_questions = [
        "Quel est le taux d'absentéisme par département pour IHEC?",
        "Combien d'enseignants à Marketing?",
        "Quelles sont les données d'EYA JABER?",
    ]
    
    for question in test_questions:
        results[question] = test_ask_question(question)
        time.sleep(1)  # Délai entre les questions
    
    # Test batch
    results["batch"] = test_batch_questions([
        "Nombre total d'enseignants IHEC?",
        "Quel est le taux de stabilité de l'équipe?"
    ])
    
    # Résumé
    print_section("RÉSUMÉ DES TESTS")
    
    passed = sum(1 for v in results.values() if v)
    total = len(results)
    
    print(f"\n✅ Réussis: {passed}/{total}")
    
    for test_name, passed in results.items():
        status = "✅" if passed else "❌"
        print(f"  {status} {test_name}")
    
    print("\n" + "="*70)
    print("✨ Tests terminés!\n")

if __name__ == "__main__":
    main()
