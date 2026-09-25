-- ============================================================
-- V1 — Schéma initial
-- Épreuve finale KFOKAM48 — présence et relecture
-- Correspond au diagramme D2 (docs/diagrammes/D2-modele-donnees.md)
-- ============================================================

-- ---------- PROMOTION ----------
CREATE TABLE promotion (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(120) NOT NULL UNIQUE,
    cree_le     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- ETUDIANT ----------
CREATE TABLE etudiant (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(80) NOT NULL,
    prenom          VARCHAR(80) NOT NULL,
    promotion_id    BIGINT NOT NULL,
    CONSTRAINT fk_etudiant_promotion
        FOREIGN KEY (promotion_id) REFERENCES promotion(id)
);

CREATE INDEX idx_etudiant_promotion ON etudiant(promotion_id);

-- ---------- SESSION ----------
CREATE TABLE session (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre           VARCHAR(200) NOT NULL,
    code            VARCHAR(6)   NOT NULL UNIQUE,
    promotion_id    BIGINT       NOT NULL,
    ouverture_at    TIMESTAMP    NOT NULL,
    expiration_at   TIMESTAMP    NOT NULL,
    cloture_at      TIMESTAMP,
    CONSTRAINT fk_session_promotion
        FOREIGN KEY (promotion_id) REFERENCES promotion(id)
);

CREATE INDEX idx_session_promotion ON session(promotion_id);
CREATE INDEX idx_session_code       ON session(code);

-- ---------- PRESENCE ----------
-- RG4 : une seule présence par (session, étudiant)
-- RG13 : source = ETUDIANT | FORMATEUR
CREATE TABLE presence (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      BIGINT      NOT NULL,
    etudiant_id     BIGINT      NOT NULL,
    source          VARCHAR(20) NOT NULL,
    marquee_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_presence_session
        FOREIGN KEY (session_id) REFERENCES session(id),
    CONSTRAINT fk_presence_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES etudiant(id),
    CONSTRAINT uk_presence_session_etudiant
        UNIQUE (session_id, etudiant_id),
    CONSTRAINT ck_presence_source
        CHECK (source IN ('ETUDIANT', 'FORMATEUR'))
);

CREATE INDEX idx_presence_session  ON presence(session_id);
CREATE INDEX idx_presence_etudiant ON presence(etudiant_id);

-- ---------- EXERCICE ----------
-- RG5 : un seul exercice par (session, étudiant)
-- Statuts : DEPOSE | EN_ATTENTE | RELUE | CLOTURE
CREATE TABLE exercice (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      BIGINT       NOT NULL,
    etudiant_id     BIGINT       NOT NULL,
    lien            VARCHAR(500) NOT NULL,
    statut          VARCHAR(20)  NOT NULL DEFAULT 'DEPOSE',
    depose_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercice_session
        FOREIGN KEY (session_id) REFERENCES session(id),
    CONSTRAINT fk_exercice_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES etudiant(id),
    CONSTRAINT uk_exercice_session_etudiant
        UNIQUE (session_id, etudiant_id),
    CONSTRAINT ck_exercice_statut
        CHECK (statut IN ('DEPOSE', 'EN_ATTENTE', 'RELUE', 'CLOTURE'))
);

CREATE INDEX idx_exercice_session  ON exercice(session_id);
CREATE INDEX idx_exercice_etudiant ON exercice(etudiant_id);
CREATE INDEX idx_exercice_statut   ON exercice(statut);

-- ---------- RELECTURE ----------
-- RG2 : pas d'auto-relecture (vérifié en service)
-- RG3 : note entière entre 0 et 20
-- RG6 : un seul relecteur par exercice
-- RG8 : le nom du relecteur n'est jamais exposé au client
CREATE TABLE relecture (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exercice_id     BIGINT       NOT NULL,
    relecteur_id    BIGINT       NOT NULL,
    note            INT,
    commentaire     VARCHAR(2000),
    statut          VARCHAR(20)  NOT NULL DEFAULT 'EN_ATTENTE',
    assignee_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rendue_at       TIMESTAMP,
    CONSTRAINT fk_relecture_exercice
        FOREIGN KEY (exercice_id) REFERENCES exercice(id),
    CONSTRAINT fk_relecture_relecteur
        FOREIGN KEY (relecteur_id) REFERENCES etudiant(id),
    CONSTRAINT uk_relecture_exercice
        UNIQUE (exercice_id),
    CONSTRAINT ck_relecture_note
        CHECK (note IS NULL OR (note >= 0 AND note <= 20)),
    CONSTRAINT ck_relecture_statut
        CHECK (statut IN ('EN_ATTENTE', 'RELUE'))
);

CREATE INDEX idx_relecture_relecteur ON relecture(relecteur_id);
CREATE INDEX idx_relecture_statut    ON relecture(statut);