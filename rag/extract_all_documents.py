"""
extract_all_documents.py — Extraction unified des 3 documents: bulletin, Excel, PDF
Création d'une structure JSON cohérente pour tous les documents
"""

import os
import sys
import json
import base64
import time
from pathlib import Path
from datetime import datetime
from dotenv import load_dotenv
from openai import OpenAI

try:
    import fitz  # PyMuPDF
    import pandas as pd
    from PIL import Image
except ImportError:
    print("❌ Packages manquants. Installation...")
    os.system("pip install pymupdf pandas pillow")

load_dotenv()

api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    print("❌ ERREUR: OPENAI_API_KEY non configurée")
    sys.exit(1)

client = OpenAI(api_key=api_key)

SERVICES_DIR = Path(__file__).parent / "app/services"
EXTRACTED_DIR = SERVICES_DIR / "extracted_data"

# ============================================================================
# 1. EXTRACTION DU BULLETIN (image)
# ============================================================================

def extract_bulletin():
    """Extrait les données du bulletin EYA JABER (image)"""
    
    print("\n" + "="*70)
    print("📸 EXTRACTION DU BULLETIN EYA JABER")
    print("="*70)
    
    bulletin_path = SERVICES_DIR / "bulletin.jpg"
    
    if not bulletin_path.exists():
        print(f"❌ Fichier non trouvé: {bulletin_path}")
        return None
    
    print(f"✅ Fichier trouvé: {bulletin_path.name}")
    
    # Encoder l'image en base64
    print("\n⏳ Lecture de l'image...")
    with open(bulletin_path, 'rb') as f:
        img_base64 = base64.b64encode(f.read()).decode()
    
    print("✅ Image encodée")
    
    # Appeler GPT-4 Vision
    print("\n⏳ Appel GPT-4 Vision pour extraction des données...")
    
    system_prompt = """Vous êtes un expert en extraction de données académiques.
Analysez ce bulletin scolaire et extrayez:
1. Nom et prénom de l'étudiant
2. Numéro d'étudiant/ID si visible
3. Toutes les matières et leurs notes
4. La moyenne générale
5. Tous les autres détails académiques

Retournez UNIQUEMENT du JSON valide:
{
    "student_name": "...",
    "student_id": "...",
    "subjects": [
        {"subject": "...", "grade": X.XX, "coefficient": X}
    ],
    "average": X.XX,
    "other_info": {}
}"""
    
    try:
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {
                    "role": "user",
                    "content": [
                        {"type": "text", "text": system_prompt},
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": f"data:image/jpeg;base64,{img_base64}",
                                "detail": "high"
                            }
                        }
                    ]
                }
            ],
            temperature=0.2,
            max_tokens=2000
        )
        
        content = response.choices[0].message.content
        print("✅ Extraction réussie")
        
        # Parser JSON
        import re
        json_match = re.search(r'\{[\s\S]*\}', content)
        if json_match:
            bulletin_data = json.loads(json_match.group())
        else:
            bulletin_data = {"raw_content": content}
        
        return bulletin_data
    
    except Exception as e:
        print(f"❌ Erreur GPT: {e}")
        return None

# ============================================================================
# 2. EXTRACTION DE L'EXCEL ISSTE
# ============================================================================

def extract_excel():
    """Extrait les données de l'Excel ISSTE"""
    
    print("\n" + "="*70)
    print("📊 EXTRACTION DE L'EXCEL ISSTE")
    print("="*70)
    
    excel_path = SERVICES_DIR / "ISSTE_Conference_Scientifique_2025.xlsx"
    
    if not excel_path.exists():
        print(f"❌ Fichier non trouvé: {excel_path}")
        return None
    
    print(f"✅ Fichier trouvé: {excel_path.name}")
    
    try:
        # Lire tous les sheets
        print("\n⏳ Lecture des sheets...")
        xls = pd.ExcelFile(excel_path)
        sheets = xls.sheet_names
        print(f"✅ {len(sheets)} sheets trouvés: {sheets}")
        
        excel_data = {
            "sheets": {}
        }
        
        for sheet_name in sheets:
            print(f"   📖 Traitement: {sheet_name}...")
            df = pd.read_excel(excel_path, sheet_name=sheet_name)
            
            # Convertir en dictionnaire
            sheet_dict = {
                "rows": df.to_dict('records'),
                "columns": df.columns.tolist(),
                "shape": df.shape
            }
            
            excel_data["sheets"][sheet_name] = sheet_dict
        
        print("✅ Excel extrait avec succès")
        return excel_data
    
    except Exception as e:
        print(f"❌ Erreur extraction Excel: {e}")
        return None

# ============================================================================
# 3. EXTRACTION DU PDF IHEC
# ============================================================================

def extract_pdf_ihec():
    """Extrait les données du PDF IHEC avec GPT-4 Vision"""
    
    print("\n" + "="*70)
    print("📄 EXTRACTION DU PDF IHEC")
    print("="*70)
    
    pdf_path = SERVICES_DIR / "generated_pdfs/rapport_rh_ihec_2024-2025.pdf"
    
    if not pdf_path.exists():
        print(f"❌ Fichier non trouvé: {pdf_path}")
        return None
    
    print(f"✅ Fichier trouvé: {pdf_path.name}")
    
    # Convertir en images
    print("\n⏳ Conversion PDF en images...")
    doc = fitz.open(pdf_path)
    total_pages = len(doc)
    print(f"   📊 Total pages: {total_pages}")
    
    images = []
    for page_num in range(min(total_pages, 5)):  # Limiter à 5 pages
        print(f"   ⏳ Page {page_num + 1}/{min(total_pages, 5)}...", end="\r")
        page = doc[page_num]
        pix = page.get_pixmap(matrix=fitz.Matrix(2, 2))
        img_base64 = base64.b64encode(pix.tobytes("png")).decode()
        images.append({"page": page_num + 1, "base64": img_base64})
    
    print(f"\n✅ {len(images)} page(s) converties")
    
    pdf_data = {
        "pages": []
    }
    
    system_prompt = """Extrayez les données RH de cette page PDF:
1. Tous les tableaux en JSON
2. Les graphiques avec leurs données
3. Les textes importants
4. Les métriques RH (taux, pourcentages, nombres)

Retournez UNIQUEMENT du JSON valide:
{
    "page_number": X,
    "title": "...",
    "tables": [...],
    "metrics": {...},
    "text_sections": [...]
}"""
    
    # Extraire avec GPT-4 Vision
    print("\n⏳ Appel GPT-4 Vision pour les pages...")
    
    for idx, img_data in enumerate(images):
        page_num = img_data["page"]
        print(f"   📖 Page {page_num}...", end=" ")
        
        try:
            response = client.chat.completions.create(
                model="gpt-4o",
                messages=[
                    {
                        "role": "user",
                        "content": [
                            {"type": "text", "text": system_prompt},
                            {
                                "type": "image_url",
                                "image_url": {
                                    "url": f"data:image/png;base64,{img_data['base64']}",
                                    "detail": "high"
                                }
                            }
                        ]
                    }
                ],
                temperature=0.2,
                max_tokens=2000
            )
            
            content = response.choices[0].message.content
            print("✅")
            
            # Parser JSON
            import re
            json_match = re.search(r'\{[\s\S]*\}', content)
            if json_match:
                page_data = json.loads(json_match.group())
            else:
                page_data = {"page_number": page_num, "raw_content": content}
            
            pdf_data["pages"].append(page_data)
            
            if idx < len(images) - 1:
                time.sleep(1)  # Délai pour éviter les limites API
        
        except Exception as e:
            print(f"❌ {e}")
            pdf_data["pages"].append({"page_number": page_num, "error": str(e)})
    
    print("✅ PDF extrait avec succès")
    return pdf_data

# ============================================================================
# 4. SAUVEGARDE CENTRALISÉE
# ============================================================================

def save_all_documents(bulletin_data, excel_data, pdf_data):
    """Sauvegarde tous les documents dans une structure cohérente"""
    
    print("\n" + "="*70)
    print("💾 SAUVEGARDE DES DONNÉES")
    print("="*70)
    
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    
    # Document bulletin
    if bulletin_data:
        bulletin_dir = EXTRACTED_DIR / "bulletin"
        bulletin_dir.mkdir(parents=True, exist_ok=True)
        
        bulletin_file = bulletin_dir / f"bulletin_eya_jaber_{timestamp}_extracted.json"
        
        with open(bulletin_file, 'w', encoding='utf-8') as f:
            json.dump({
                "metadata": {
                    "type": "bulletin",
                    "institution_id": "bulletin",
                    "extraction_date": datetime.now().isoformat(),
                    "source": "bulletin.jpg"
                },
                "data": bulletin_data
            }, f, indent=2, ensure_ascii=False)
        
        print(f"✅ Bulletin: {bulletin_file}")
    
    # Document Excel
    if excel_data:
        excel_dir = EXTRACTED_DIR / "isste"
        excel_dir.mkdir(parents=True, exist_ok=True)
        
        excel_file = excel_dir / f"ISSTE_Conference_{timestamp}_extracted.json"
        
        with open(excel_file, 'w', encoding='utf-8') as f:
            json.dump({
                "metadata": {
                    "type": "conference",
                    "institution_id": "isste",
                    "extraction_date": datetime.now().isoformat(),
                    "source": "ISSTE_Conference_Scientifique_2025.xlsx"
                },
                "data": excel_data
            }, f, indent=2, ensure_ascii=False)
        
        print(f"✅ Excel: {excel_file}")
    
    # Document PDF
    if pdf_data:
        pdf_dir = EXTRACTED_DIR / "ihec"
        pdf_dir.mkdir(parents=True, exist_ok=True)
        
        pdf_file = pdf_dir / f"rapport_rh_ihec_{timestamp}_extracted.json"
        
        with open(pdf_file, 'w', encoding='utf-8') as f:
            json.dump({
                "metadata": {
                    "type": "rapport_rh",
                    "institution_id": "ihec",
                    "extraction_date": datetime.now().isoformat(),
                    "source": "rapport_rh_ihec_2024-2025.pdf"
                },
                "data": pdf_data
            }, f, indent=2, ensure_ascii=False)
        
        print(f"✅ PDF IHEC: {pdf_file}")

# ============================================================================
# 5. MAIN
# ============================================================================

def main():
    print("\n" + "="*70)
    print("🚀 EXTRACTION UNIFIÉE - TOUS LES DOCUMENTS")
    print("="*70)
    
    # Extraire les 3 documents
    bulletin_data = extract_bulletin()
    time.sleep(2)
    
    excel_data = extract_excel()
    time.sleep(2)
    
    pdf_data = extract_pdf_ihec()
    
    # Sauvegarder
    save_all_documents(bulletin_data, excel_data, pdf_data)
    
    print("\n" + "="*70)
    print("✅ EXTRACTION COMPLÈTE")
    print("="*70)
    print("\n💡 Les données sont prêtes pour les endpoints RAG!\n")

if __name__ == "__main__":
    main()
