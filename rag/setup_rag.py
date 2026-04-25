"""
setup_rag.py — Setup guidé du système RAG
"""

import subprocess
import sys
import os
from pathlib import Path

def print_header(text):
    print("\n" + "="*70)
    print(f"✨ {text}")
    print("="*70)

def print_step(num, text):
    print(f"\n{num}️⃣  {text}")

def run_command(cmd, cwd=None):
    """Exécute une commande et retourne le succès"""
    try:
        result = subprocess.run(cmd, shell=True, cwd=cwd, capture_output=True, text=True, timeout=600)
        if result.returncode == 0:
            print("✅ Succès")
            return True
        else:
            print(f"❌ Erreur: {result.stderr}")
            return False
    except Exception as e:
        print(f"❌ Exception: {e}")
        return False

def main():
    backend_dir = Path(__file__).parent
    
    print_header("🚀 SETUP RAG SYSTEM")
    
    print("""
    Ce setup va:
    1. Extraire les 3 documents (bulletin, Excel, PDF)
    2. Démarrer l'API FastAPI
    3. Tester les endpoints
    
    ⏱️  Total: ~5-10 minutes (selon la vitesse d'extraction)
    """)
    
    # STEP 1: Vérifier les fichiers
    print_step(1, "Vérification des fichiers source")
    
    files_to_check = [
        "app/services/bulletin.jpg",
        "app/services/ISSTE_Conference_Scientifique_2025.xlsx",
        "app/services/generated_pdfs/rapport_rh_ihec_2024-2025.pdf"
    ]
    
    missing = []
    for file in files_to_check:
        full_path = backend_dir / file
        if full_path.exists():
            print(f"  ✅ {file}")
        else:
            print(f"  ❌ {file} - MANQUANT")
            missing.append(file)
    
    if missing:
        print(f"\n⚠️  {len(missing)} fichier(s) manquant(s)")
        return False
    
    # STEP 2: Extraction
    print_step(2, "Extraction des documents")
    print("  ⏳ Cela peut prendre 3-5 minutes (appels GPT-4 Vision)...\n")
    
    if not run_command("python extract_all_documents.py", cwd=backend_dir):
        print("\n❌ Extraction échouée!")
        return False
    
    # Vérifier les fichiers extraits
    print("\n  Vérification des fichiers extraits...")
    extracted_dir = backend_dir / "app/services/extracted_data"
    
    if not extracted_dir.exists():
        print("  ❌ Aucun fichier extrait trouvé!")
        return False
    
    json_files = list(extracted_dir.glob("**/*_extracted.json"))
    print(f"  ✅ {len(json_files)} fichier(s) extrait(s)")
    
    for jf in json_files:
        rel_path = jf.relative_to(backend_dir)
        print(f"     • {rel_path}")
    
    # STEP 3: Démarrer l'API
    print_step(3, "Démarrage de l'API FastAPI")
    print("  Lancez dans un NOUVEAU TERMINAL:")
    print()
    print("  " + "="*60)
    print("  cd backend")
    print("  python -m uvicorn app.main:app --reload --port 8000")
    print("  " + "="*60)
    print()
    
    input("  Appuyez sur ENTER une fois que l'API est démarrée...")
    
    # STEP 4: Tests
    print_step(4, "Tests des endpoints")
    
    try:
        import requests
        
        # Test santé
        response = requests.get("http://localhost:8000/health", timeout=5)
        if response.status_code == 200:
            print("  ✅ Santé API")
        else:
            print("  ❌ Santé API échouée")
            return False
        
        # Test documents
        response = requests.get("http://localhost:8000/api/rag/documents", timeout=10)
        if response.status_code == 200:
            data = response.json()
            print(f"  ✅ Documents chargés: {data.get('total', 0)} document(s)")
        else:
            print("  ❌ Chargement documents échoué")
            return False
        
        # Test question
        print("\n  Test d'une question...")
        response = requests.post(
            "http://localhost:8000/api/rag/ask",
            params={"question": "Quel est le taux d'absentéisme IHEC?"},
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success", True):
                print("  ✅ Question réussie")
                print(f"\n  Question: {data.get('question')}")
                print(f"  Réponse: {data.get('answer')[:100]}...")
                print(f"  Sources: {', '.join(data.get('sources', []))}")
            else:
                print(f"  ❌ Erreur: {data.get('error')}")
                return False
        else:
            print(f"  ❌ HTTP {response.status_code}")
            return False
    
    except ImportError:
        print("  ⚠️  requests non installé, test skippé")
    except Exception as e:
        print(f"  ❌ Erreur: {e}")
        return False
    
    # SUCCESS!
    print_header("✅ SETUP RÉUSSI!")
    
    print("""
    Prochaines étapes:
    
    1. L'API est démarrée sur http://localhost:8000
    
    2. Testez des questions:
       curl "http://localhost:8000/api/rag/ask?question=Combien%20d%20enseignants%20marketing"
    
    3. Consultez la doc:
       cat RAG_DOCUMENTATION.md
    
    4. Lancez les tests complets:
       python test_rag_endpoints.py
    
    📖 Exemples de questions:
       • Quel est le taux d'absentéisme par département?
       • Quelles sont les notes d'EYA JABER?
       • Combien d'enseignants à ISSTE?
       • Résume les données IHEC
    
    """)
    
    return True

if __name__ == "__main__":
    try:
        success = main()
        sys.exit(0 if success else 1)
    except KeyboardInterrupt:
        print("\n\n❌ Annulé par l'utilisateur")
        sys.exit(1)
