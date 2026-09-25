-- ============================================================
-- V3 — Passage a 2 relecteurs par exercice
-- Consequence du changement de besoin (enveloppe etape 3).
-- NE MODIFIE PAS V1 ni V2. Ajoute uniquement.
-- ============================================================

-- 1) Supprimer la contrainte UNIQUE(exercice_id) sur relecture
ALTER TABLE relecture DROP CONSTRAINT IF EXISTS uk_relecture_exercice;

-- 2) Ajouter une contrainte d'unicite sur (exercice_id, relecteur_id)
--    pour eviter qu'un meme etudiant relit 2 fois le meme exercice
ALTER TABLE relecture
    ADD CONSTRAINT uk_relecture_exercice_relecteur
    UNIQUE (exercice_id, relecteur_id);

-- 3) Ajouter un statut "PARTIELLEMENT_RELUE" pour les exercices
--    qui n'ont qu'une seule relecture rendue sur les deux
-- (pas de changement de schema, le champ statut est un VARCHAR libre)
-- La contrainte CHECK existante est mise a jour :

ALTER TABLE exercice DROP CONSTRAINT IF EXISTS ck_exercice_statut;

ALTER TABLE exercice
    ADD CONSTRAINT ck_exercice_statut
    CHECK (statut IN ('DEPOSE', 'EN_ATTENTE', 'PARTIELLEMENT_RELUE', 'RELUE', 'CLOTURE'));