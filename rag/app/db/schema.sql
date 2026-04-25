CREATE TABLE IF NOT EXISTS institutions (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    type TEXT,
    location TEXT,
    created_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS periods (
    id TEXT PRIMARY KEY,
    label TEXT,
    start_date TEXT,
    end_date TEXT
);

CREATE TABLE IF NOT EXISTS source_artifacts (
    source_id      TEXT PRIMARY KEY,
    source_type    TEXT,
    file_name      TEXT,
    uploaded_by    TEXT,
    uploaded_at    TEXT,
    parser_type    TEXT,
    parser_version TEXT DEFAULT '1.0',
    institution_id TEXT,
    tenant_id      TEXT DEFAULT 'ucar',
    FOREIGN KEY (institution_id) REFERENCES institutions(id)
);

CREATE TABLE IF NOT EXISTS business_records (
    record_id        TEXT PRIMARY KEY,
    tenant_id        TEXT DEFAULT 'ucar',
    institution_id   TEXT NOT NULL,
    domain           TEXT NOT NULL,
    period_id        TEXT NOT NULL,
    process          TEXT,
    entity_type      TEXT,
    entity_id        TEXT,
    attribute_name   TEXT NOT NULL,
    value_type       TEXT DEFAULT 'numeric',
    value_numeric    REAL,
    value_text       TEXT,
    value_unit       TEXT,
    source_id        TEXT,
    confidence_score REAL DEFAULT 1.0,
    extraction_origin TEXT,
    created_at       TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (institution_id) REFERENCES institutions(id),
    FOREIGN KEY (source_id) REFERENCES source_artifacts(source_id)
);

CREATE TABLE IF NOT EXISTS kpi_definitions (
    kpi_id             TEXT PRIMARY KEY,
    kpi_code           TEXT UNIQUE NOT NULL,
    name               TEXT NOT NULL,
    domain             TEXT,
    unit               TEXT,
    aggregation_type   TEXT,
    direction          TEXT,
    warning_threshold  REAL,
    critical_threshold REAL
);

CREATE TABLE IF NOT EXISTS kpi_observations (
    observation_id TEXT PRIMARY KEY,
    kpi_id         TEXT NOT NULL,
    kpi_code       TEXT,
    institution_id TEXT NOT NULL,
    period_id      TEXT NOT NULL,
    scope_type     TEXT DEFAULT 'institution',
    scope_id       TEXT,
    computed_value REAL,
    unit           TEXT,
    trend          TEXT DEFAULT 'stable',
    computed_at    TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (kpi_id) REFERENCES kpi_definitions(kpi_id)
);

CREATE TABLE IF NOT EXISTS alerts (
    alert_id        TEXT PRIMARY KEY,
    type            TEXT,
    severity        TEXT,
    kpi_id          TEXT,
    kpi_code        TEXT,
    institution_id  TEXT,
    period_id       TEXT,
    observed_value  REAL,
    threshold_value REAL,
    message         TEXT,
    status          TEXT DEFAULT 'open',
    created_at      TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS students (
    id               TEXT PRIMARY KEY,
    numero_etudiant  TEXT,
    nom              TEXT,
    prenom           TEXT,
    cin              TEXT,
    date_naissance   TEXT,
    institution_id   TEXT,
    filiere          TEXT,
    specialite       TEXT,
    niveau           TEXT,
    annee_univ       TEXT,
    groupe           TEXT,
    created_at       TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS student_notes (
    id          TEXT PRIMARY KEY,
    student_id  TEXT,
    matiere     TEXT,
    note_ds     REAL,
    note_exam   REAL,
    note_tp     REAL,
    moyenne     REAL,
    coefficient REAL,
    credits     REAL,
    resultat    TEXT,
    period_id   TEXT,
    created_at  TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS recommendations (
    recommendation_id TEXT PRIMARY KEY,
    institution_id    TEXT,
    domain            TEXT,
    period_id         TEXT,
    category          TEXT,
    priority          TEXT,
    title             TEXT,
    description       TEXT,
    justification     TEXT,
    generated_by      TEXT DEFAULT 'llm',
    created_at        TEXT DEFAULT (datetime('now'))
);

-- Données de référence
INSERT OR IGNORE INTO institutions VALUES
('enstab','ENSTAB Bizerte','école','Bizerte','2024-01-01'),
('fseg','FSEG Nabeul','faculté','Nabeul','2024-01-01'),
('ihec','IHEC Carthage','école','Carthage','2024-01-01'),
('isste','ISSTE Borj Cedria','institut','Borj Cedria','2024-01-01');

INSERT OR IGNORE INTO periods VALUES
('2024-S1','Semestre 1 2024','2024-01-01','2024-06-30'),
('2024-S2','Semestre 2 2024','2024-07-01','2024-12-31'),
('2024-2025','Année 2024-2025','2024-09-01','2025-06-30'),
('2025-S1','Semestre 1 2025','2025-01-01','2025-06-30');

INSERT OR IGNORE INTO kpi_definitions VALUES
('kpi-acad-001','SUCCESS_RATE','Taux de réussite','academic','percent','avg','higher_is_better',60,50),
('kpi-acad-002','ATTENDANCE_RATE','Taux de présence','academic','percent','avg','higher_is_better',75,60),
('kpi-acad-003','GRADE_REPETITION_RATE','Taux de redoublement','academic','percent','avg','lower_is_better',15,25),
('kpi-acad-004','DROPOUT_RATE','Taux d''abandon','academic','percent','avg','lower_is_better',15,25),
('kpi-acad-005','PEDAGOGICAL_PROGRESSION','Progression pédagogique','academic','boolean','ratio','higher_is_better',null,null),
('kpi-acad-006','EXAM_RESULTS','Résultats examens','academic','score_20','avg','higher_is_better',10,8),
('kpi-acad-007','STUDENT_COUNT','Effectif étudiants','academic','count','sum','higher_is_better',null,null),
('kpi-fin-001','BUDGET_ALLOCATED','Budget alloué','finance','TND','sum','target_is_best',null,null),
('kpi-fin-002','BUDGET_CONSUMED','Budget consommé','finance','TND','sum','target_is_best',null,null),
('kpi-fin-003','BUDGET_EXEC_RATE','Taux d''exécution budgétaire','finance','percent','avg','higher_is_better',70,50),
('kpi-fin-004','COST_PER_STUDENT','Coût par étudiant','finance','TND','avg','lower_is_better',8000,12000),
('kpi-hr-001','HEADCOUNT_TEACHING','Effectif enseignant','hr','count','sum','higher_is_better',null,null),
('kpi-hr-002','HEADCOUNT_ADMIN','Effectif administratif','hr','count','sum','higher_is_better',null,null),
('kpi-hr-003','ABSENTEEISM_RATE','Taux d''absentéisme','hr','percent','avg','lower_is_better',8,12),
('kpi-hr-004','TRAINING_COMPLETED','Formations complétées','hr','count','sum','higher_is_better',null,null),
('kpi-hr-005','TEACHING_LOAD','Charge horaire','hr','h/week','avg','target_is_best',null,null),
('kpi-res-001','PUBLICATIONS_TOTAL','Publications totales','research','count','sum','higher_is_better',null,null),
('kpi-res-002','ACTIVE_PROJECTS','Projets actifs','research','count','sum','higher_is_better',null,null),
('kpi-res-003','FUNDING_SECURED','Financements sécurisés','research','TND','sum','higher_is_better',null,null),
('kpi-res-004','PATENTS_FILED','Brevets déposés','research','count','sum','higher_is_better',null,null),
('kpi-esg-001','ENERGY_CONSUMPTION','Consommation énergie','esg','MWh','sum','lower_is_better',null,null),
('kpi-esg-002','CARBON_FOOTPRINT','Empreinte carbone','esg','tCO2e','sum','lower_is_better',null,null),
('kpi-esg-003','RECYCLING_RATE','Taux de recyclage','esg','percent','avg','higher_is_better',40,20),
('kpi-esg-004','SUSTAINABLE_MOBILITY','Mobilité durable','esg','percent','avg','higher_is_better',60,40),
('kpi-infra-001','CLASSROOM_OCCUPANCY','Taux occupation salles','infrastructure','percent','avg','higher_is_better',70,50),
('kpi-infra-002','IT_AVAILABILITY','Disponibilité informatique','infrastructure','percent','avg','higher_is_better',90,80),
('kpi-infra-003','ONGOING_WORKS','Travaux en cours','infrastructure','count','sum','target_is_best',null,null),
('kpi-part-001','ACTIVE_AGREEMENTS','Accords actifs','partnerships','count','sum','higher_is_better',null,null),
('kpi-part-002','OUTGOING_MOBILITY','Mobilité sortante','partnerships','count','sum','higher_is_better',null,null),
('kpi-part-003','INCOMING_MOBILITY','Mobilité entrante','partnerships','count','sum','higher_is_better',null,null),
('kpi-part-004','INTERNATIONAL_PROJECTS','Projets internationaux','partnerships','count','sum','higher_is_better',null,null);