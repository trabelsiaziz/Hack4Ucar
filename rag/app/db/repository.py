"""
repository.py — Sauvegarde tous les objets canoniques en base
"""
import sqlite3, uuid
from app.db.database import get_connection
from datetime import datetime, timezone

def _now():
    return datetime.now(timezone.utc).isoformat()

def save_source_artifact(artifact: dict):
    conn = get_connection()
    try:
        conn.execute("""
            INSERT OR REPLACE INTO source_artifacts
            (source_id, source_type, file_name, uploaded_by,
             uploaded_at, parser_type, institution_id, tenant_id)
            VALUES (?,?,?,?,?,?,?,?)
        """, (
            artifact["source_id"], artifact.get("source_type","pdf"),
            artifact["file_name"], artifact.get("uploaded_by","system"),
            artifact.get("uploaded_at", _now()),
            artifact.get("parser_type","extractor-v1"),
            artifact.get("institution_id"), artifact.get("tenant_id","ucar")
        ))
        conn.commit()
    finally:
        conn.close()

def save_business_records(records: list):
    conn = get_connection()
    try:
        for r in records:
            # Supporte ancien format plat ET nouveau format canonique
            if "context" in r:
                inst   = r["context"]["institution_id"]
                domain = r["context"]["domain"]
                period = r["context"]["period_id"]
                val    = r.get("value", {}).get("numeric")
                unit   = r.get("value", {}).get("unit")
                src_id = r.get("source_ref", {}).get("source_id")
                conf   = r.get("source_ref", {}).get("confidence_score", 1.0)
                origin = r.get("audit_meta", {}).get("produced_by","extractor")
            else:
                inst   = r.get("institution_id")
                domain = r.get("domain","unknown")
                period = r.get("period_id","unknown")
                val    = r.get("value_numeric")
                unit   = r.get("value_unit")
                src_id = r.get("source_id")
                conf   = r.get("confidence_score",1.0)
                origin = r.get("extraction_origin","extractor")

            conn.execute("""
                INSERT OR REPLACE INTO business_records
                (record_id, tenant_id, institution_id, domain, period_id,
                 process, entity_type, entity_id, attribute_name,
                 value_type, value_numeric, value_unit,
                 source_id, confidence_score, extraction_origin, created_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """, (
                r.get("record_id", f"rec-{uuid.uuid4().hex[:8]}"),
                r.get("tenant_id","ucar"),
                inst, domain, period,
                r.get("process","general"),
                r.get("entity_type","entity"),
                r.get("entity_id","global"),
                r.get("attribute_name"),
                r.get("value_type","numeric"),
                val, unit, src_id, conf, origin, _now()
            ))
        conn.commit()
        return len(records)
    finally:
        conn.close()

def save_kpi_observations(observations: list):
    conn = get_connection()
    try:
        for obs in observations:
            ctx = obs.get("context", {})
            conn.execute("""
                INSERT OR REPLACE INTO kpi_observations
                (observation_id, kpi_id, kpi_code, institution_id,
                 period_id, scope_type, scope_id, computed_value,
                 unit, trend, computed_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """, (
                obs.get("observation_id", f"obs-{uuid.uuid4().hex[:8]}"),
                obs.get("kpi_id","kpi-unknown"),
                obs.get("kpi_code","UNKNOWN"),
                ctx.get("institution_id", obs.get("institution_id")),
                ctx.get("period_id", obs.get("period_id")),
                obs.get("scope_type","institution"),
                obs.get("scope_id"),
                obs.get("computed_value"),
                obs.get("unit"),
                obs.get("trend","stable"),
                obs.get("computed_at", _now())
            ))
        conn.commit()
        return len(observations)
    finally:
        conn.close()

def save_alerts(alerts: list):
    conn = get_connection()
    try:
        for a in alerts:
            ctx = a.get("context", {})
            conn.execute("""
                INSERT OR REPLACE INTO alerts
                (alert_id, type, severity, kpi_id, kpi_code,
                 institution_id, period_id, observed_value,
                 threshold_value, message, status, created_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """, (
                a.get("alert_id", f"alt-{uuid.uuid4().hex[:8]}"),
                a.get("type","threshold_breach"),
                a.get("severity","medium"),
                a.get("kpi_id"), a.get("kpi_code"),
                ctx.get("institution_id", a.get("institution_id")),
                ctx.get("period_id", a.get("period_id")),
                a.get("observed_value"),
                a.get("threshold_value"),
                a.get("message",""),
                a.get("status","open"),
                _now()
            ))
        conn.commit()
        return len(alerts)
    finally:
        conn.close()

def save_student(bulletin: dict) -> str:
    conn = get_connection()
    try:
        s   = bulletin.get("student", {})
        ins = bulletin.get("inscription", {})
        sid = f"stu-{uuid.uuid4().hex[:8]}"
        conn.execute("""
            INSERT OR REPLACE INTO students
            (id, numero_etudiant, nom, prenom, cin, date_naissance,
             institution_id, filiere, specialite, niveau, annee_univ, groupe)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """, (
            sid,
            s.get("numero_etudiant"), s.get("nom"), s.get("prenom"),
            s.get("cin"), s.get("date_naissance"),
            ins.get("institution","").lower().replace(" ","_")[:20],
            ins.get("filiere"), ins.get("specialite"),
            ins.get("niveau"), ins.get("annee_universitaire"),
            ins.get("groupe")
        ))
        for n in bulletin.get("notes", []):
            conn.execute("""
                INSERT INTO student_notes
                (id, student_id, matiere, note_ds, note_exam, note_tp,
                 moyenne, coefficient, credits, resultat, period_id)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """, (
                f"note-{uuid.uuid4().hex[:8]}", sid,
                n.get("matiere"), n.get("note_ds"), n.get("note_exam"),
                n.get("note_tp"), n.get("moyenne"), n.get("coefficient"),
                n.get("credits"), n.get("resultat"),
                ins.get("annee_universitaire","2024-2025")
            ))
        conn.commit()
        return sid
    finally:
        conn.close()

# ── Requêtes lecture ──

def get_kpi_observations(institution_id=None, domain=None, period_id=None):
    conn = get_connection()
    q = "SELECT * FROM kpi_observations WHERE 1=1"
    params = []
    if institution_id:
        q += " AND institution_id=?"; params.append(institution_id)
    if domain:
        q += " AND kpi_code LIKE ?"; params.append(f"%-{domain.upper()[:3]}%")
    if period_id:
        q += " AND period_id=?"; params.append(period_id)
    rows = conn.execute(q, params).fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_alerts(status="open", institution_id=None):
    conn = get_connection()
    q = "SELECT * FROM alerts WHERE status=?"
    params = [status]
    if institution_id:
        q += " AND institution_id=?"; params.append(institution_id)
    q += " ORDER BY created_at DESC"
    rows = conn.execute(q, params).fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_institutions():
    conn = get_connection()
    rows = conn.execute("SELECT * FROM institutions").fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_dashboard_summary():
    conn = get_connection()
    summary = {}
    summary["total_records"]  = conn.execute("SELECT COUNT(*) FROM business_records").fetchone()[0]
    summary["total_kpis"]     = conn.execute("SELECT COUNT(*) FROM kpi_observations").fetchone()[0]
    summary["open_alerts"]    = conn.execute("SELECT COUNT(*) FROM alerts WHERE status='open'").fetchone()[0]
    summary["total_students"] = conn.execute("SELECT COUNT(*) FROM students").fetchone()[0]
    summary["institutions"]   = conn.execute("SELECT COUNT(*) FROM institutions").fetchone()[0]
    by_severity = conn.execute("""
        SELECT severity, COUNT(*) as cnt FROM alerts
        WHERE status='open' GROUP BY severity
    """).fetchall()
    summary["alerts_by_severity"] = {r["severity"]: r["cnt"] for r in by_severity}
    by_domain = conn.execute("""
        SELECT domain, COUNT(*) as cnt FROM business_records GROUP BY domain
    """).fetchall()
    summary["records_by_domain"] = {r["domain"]: r["cnt"] for r in by_domain}
    conn.close()
    return summary