"""
rag_service.py — Service RAG centralisé pour les endpoints FastAPI
Gère le chargement, l'extraction et les réponses aux questions
"""

import json
import glob
from pathlib import Path
from typing import Dict, List, Optional
from openai import OpenAI
from dotenv import load_dotenv
import os

load_dotenv()

api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("❌ OPENAI_API_KEY non configurée")

client = OpenAI(api_key=api_key)

SERVICES_DIR = Path(__file__).parent
EXTRACTED_DIR = SERVICES_DIR / "extracted_data"


class RAGService:
    """Service RAG pour les questions/réponses basé sur les documents extraits"""
    
    def __init__(self):
        self.documents = {}
        self.load_documents()
    
    def load_documents(self):
        """Charge tous les fichiers JSON extraits"""
        
        pattern = str(EXTRACTED_DIR / "**/*_extracted.json")
        files = glob.glob(pattern, recursive=True)
        
        print(f"📂 Chargement {len(files)} document(s)...")
        
        for file_path in files:
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    rel_path = Path(file_path).relative_to(SERVICES_DIR)
                    self.documents[str(rel_path)] = data
                    
                    # Infos
                    metadata = data.get("metadata", {})
                    doc_type = metadata.get("type", "unknown")
                    institution = metadata.get("institution_id", "unknown")
                    print(f"  ✅ {doc_type} ({institution})")
            
            except Exception as e:
                print(f"  ❌ Erreur {file_path}: {e}")
        
        print(f"✅ {len(self.documents)} document(s) chargé(s)\n")
    
    def extract_relevant_data(self, question: str) -> Dict:
        """Extrait les données pertinentes à la question"""
        
        relevant_data = {
            "tables": [],
            "metrics": [],
            "texts": [],
            "sources": []
        }
        
        # Déterminer les institutions pertinentes
        institutions = []
        if any(word in question.lower() for word in ["ihec", "rapport", "absenteim", "departement"]):
            institutions.append("ihec")
        if any(word in question.lower() for word in ["isste", "conference", "conference scientifique"]):
            institutions.append("isste")
        if any(word in question.lower() for word in ["eya", "jaber", "bulletin", "etudiant", "notes", "moyenne"]):
            institutions.append("bulletin")
        
        # Si aucune institution détectée, chercher dans tous
        search_all = len(institutions) == 0
        
        for doc_path, doc_data in self.documents.items():
            # Filtrer par institution si nécessaire
            if not search_all:
                if not any(inst in doc_path.lower() for inst in institutions):
                    continue
            
            metadata = doc_data.get("metadata", {})
            institution = metadata.get("institution_id", "")
            doc_type = metadata.get("type", "")
            
            data = doc_data.get("data", {})
            
            # ===== BULLETIN =====
            if "bulletin" in institution.lower():
                if isinstance(data, dict):
                    # Ajouter les infos étudiant
                    if "student_name" in data:
                        relevant_data["texts"].append({
                            "type": "student_info",
                            "content": data,
                            "source": "bulletin"
                        })
                    
                    # Sujets/Notes
                    if "subjects" in data and isinstance(data["subjects"], list):
                        relevant_data["tables"].append({
                            "title": "Sujets et Notes - Bulletin",
                            "content": data["subjects"],
                            "source": "bulletin"
                        })
                    
                    # Moyenne
                    if "average" in data:
                        relevant_data["metrics"].append({
                            "metric": "average",
                            "value": data["average"],
                            "source": "bulletin"
                        })
            
            # ===== ISSTE (Excel) =====
            elif "isste" in institution.lower():
                sheets = data.get("sheets", {})
                for sheet_name, sheet_data in sheets.items():
                    if isinstance(sheet_data, dict) and "rows" in sheet_data:
                        relevant_data["tables"].append({
                            "title": f"ISSTE - {sheet_name}",
                            "content": sheet_data["rows"][:10],  # Limiter à 10 rows
                            "total_rows": len(sheet_data.get("rows", [])),
                            "source": f"isste/{sheet_name}"
                        })
            
            # ===== IHEC (PDF) =====
            elif "ihec" in institution.lower():
                pages = data.get("pages", [])
                for page in pages:
                    if isinstance(page, dict):
                        page_num = page.get("page_number", "?")
                        
                        # ✅ NOUVEAU FORMAT: tables avec headers et rows
                        if "tables" in page and isinstance(page["tables"], list):
                            for table in page["tables"]:
                                if isinstance(table, dict):
                                    title = table.get("title", "Tableau IHEC")
                                    headers = table.get("headers", [])
                                    rows = table.get("rows", [])
                                    
                                    # Convertir en format lisible
                                    formatted_rows = []
                                    for row in rows:
                                        if isinstance(row, list) and len(headers) > 0:
                                            row_dict = dict(zip(headers, row))
                                            formatted_rows.append(row_dict)
                                        else:
                                            formatted_rows.append(row)
                                    
                                    relevant_data["tables"].append({
                                        "title": title,
                                        "content": formatted_rows,
                                        "source": f"ihec/page_{page_num}"
                                    })
                        
                        # ✅ ANCIEN FORMAT: extracted_metrics array
                        if "extracted_metrics" in page and isinstance(page["extracted_metrics"], list):
                            for metric in page["extracted_metrics"]:
                                relevant_data["metrics"].append({
                                    "metric_name": metric.get("metric_name"),
                                    "value": metric.get("value"),
                                    "department": metric.get("department"),
                                    "source": f"ihec/page_{page_num}"
                                })
                        
                        # Texte
                        if "text_sections" in page and isinstance(page["text_sections"], list):
                            relevant_data["texts"].append({
                                "title": f"Page {page_num}",
                                "content": page["text_sections"],
                                "source": "ihec"
                            })
        
        return relevant_data
    
    def ask_question(self, question: str) -> Dict:
        """Pose une question et retourne la réponse basée sur les données"""
        
        # Extraire les données pertinentes
        relevant_data = self.extract_relevant_data(question)
        
        if not (relevant_data["tables"] or relevant_data["metrics"] or relevant_data["texts"]):
            return {
                "question": question,
                "answer": "❌ Aucune donnée pertinente trouvée pour cette question.",
                "sources": []
            }
        
        # Préparer le contexte pour GPT-4
        context_str = json.dumps(relevant_data, indent=2, ensure_ascii=False, default=str)
        
        system_prompt = """Vous êtes un assistant expert en données académiques et RH.
On vous fournit des données structurées extraites de documents (bulletins, PDFs, Excel).

RÈGLES STRICTES:
1. Répondez UNIQUEMENT basé sur ces données
2. Si une donnée n'existe pas, dites clairement "Cette information n'est pas disponible"
3. NE JAMAIS inventer de chiffres
4. Citez toujours les sources
5. Formatez la réponse de manière claire et professionnelle
6. Utilisez les vraies données des tableaux fournis"""
        
        user_prompt = f"""Données disponibles (structurées):
{context_str}

Question: {question}

Répondez de manière claire et professionnelle."""
        
        try:
            response = client.chat.completions.create(
                model="gpt-4o",
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": user_prompt}
                ],
                temperature=0.3,
                max_tokens=2000
            )
            
            answer = response.choices[0].message.content
            
            # Récupérer les sources
            sources = list(set([
                item.get("source", "unknown") for item in 
                relevant_data["tables"] + relevant_data["metrics"] + relevant_data["texts"]
            ]))
            
            return {
                "question": question,
                "answer": answer,
                "sources": sources,
                "success": True
            }
        
        except Exception as e:
            return {
                "question": question,
                "answer": f"❌ Erreur: {str(e)}",
                "sources": [],
                "success": False
            }


# Singleton instance
_rag_service: Optional[RAGService] = None


def get_rag_service() -> RAGService:
    """Retourne l'instance du service RAG"""
    global _rag_service
    if _rag_service is None:
        _rag_service = RAGService()
    return _rag_service
