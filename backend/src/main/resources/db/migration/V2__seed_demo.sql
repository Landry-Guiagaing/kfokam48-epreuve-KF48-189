-- ============================================================
-- V2 — Données de démonstration
-- Objectif : que le correcteur puisse tester immédiatement
-- sans avoir à tout saisir manuellement.
-- ============================================================

-- ---------- PROMOTION ----------
INSERT INTO promotion (nom) VALUES ('KF48-2026');

-- ---------- ÉTUDIANTS (12 pour la démo) ----------
-- On garde 12 étudiants : suffisant pour tester, pas trop lourd.
INSERT INTO etudiant (nom, prenom, promotion_id) VALUES
  ('Guiagaing',  'Landry',   1),
  ('Mbarga',     'Alice',    1),
  ('Ngo',        'Bertrand', 1),
  ('Fotso',      'Carine',   1),
  ('Tchoumi',    'David',    1),
  ('Kamdem',     'Estelle',  1),
  ('Nkoulou',    'Franck',   1),
  ('Essomba',    'Grace',    1),
  ('Mvondo',     'Hervé',    1),
  ('Atangana',   'Ines',     1),
  ('Biya',       'Junior',   1),
  ('Etoa',       'Kevine',   1);

-- ---------- SESSION DE DÉMO ----------
-- Code à 6 caractères, ouverture maintenant, expiration dans 15 min
INSERT INTO session (titre, code, promotion_id, ouverture_at, expiration_at)
VALUES (
  'Session de démonstration',
  'DEMO01',
  1,
  CURRENT_TIMESTAMP,
  DATEADD('MINUTE', 15, CURRENT_TIMESTAMP)
);

-- ---------- PRÉSENCES DE DÉMO ----------
-- 6 étudiants présents (source ETUDIANT)
INSERT INTO presence (session_id, etudiant_id, source) VALUES
  (1, 1, 'ETUDIANT'),
  (1, 2, 'ETUDIANT'),
  (1, 3, 'ETUDIANT'),
  (1, 4, 'ETUDIANT'),
  (1, 5, 'ETUDIANT'),
  (1, 6, 'ETUDIANT');

-- 1 présence ajoutée manuellement par le formateur (RG13)
INSERT INTO presence (session_id, etudiant_id, source) VALUES
  (1, 7, 'FORMATEUR');

-- ---------- EXERCICES DE DÉMO ----------
INSERT INTO exercice (session_id, etudiant_id, lien, statut) VALUES
  (1, 1, 'https://github.com/landry/exercice-1', 'EN_ATTENTE'),
  (1, 2, 'https://github.com/alice/exercice-1',  'EN_ATTENTE'),
  (1, 3, 'https://github.com/bertrand/ex1',      'EN_ATTENTE'),
  (1, 4, 'https://github.com/carine/ex1',        'EN_ATTENTE');

-- ---------- RELECTURES DE DÉMO ----------
-- Chaque exercice est assigné à un autre étudiant présent
-- (pas d'auto-relecture, RG2 respectée)
INSERT INTO relecture (exercice_id, relecteur_id, statut) VALUES
  (1, 2, 'EN_ATTENTE'),   -- Landry relu par Alice
  (2, 3, 'EN_ATTENTE'),   -- Alice relue par Bertrand
  (3, 4, 'EN_ATTENTE'),   -- Bertrand relu par Carine
  (4, 5, 'EN_ATTENTE');   -- Carine relue par David