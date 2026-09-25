# Changelog

Toutes les modifications notables de ce projet.
Format : [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/).

## [1.0.0] — 2026-09-25

### Ajouté
- Ouvrir une session avec un code de présence (EF1, RG1)
- Marquer sa présence avec un code (EF3, RG1, RG4, RG15)
- Déposer le lien de son exercice (EF5, RG5, RG11)
- Assigner automatiquement 2 relecteurs par exercice (EF7, EF15, RG2, RG7)
- Rendre une note et un commentaire (EF8, RG3, RG9)
- Consulter la note et le commentaire, sans le nom du relecteur (EF10, RG8)
- Tableau de bord formateur par promotion (EF11, RG16, ENF2)
- Clôturer une session (EF2, RG9, RG15)
- Note finale = moyenne des 2 relectures, note provisoire si 1 seule (EF16, EF17, RG17, RG18)
- Frontend React : 3 écrans (formateur, étudiant, relecteur)
- Dockerfile + docker-compose.yml
- Migrations Flyway V1 (schéma), V2 (démo), V3 (double relecture)
- Tests unitaires et d'intégration (dont test de concurrence)

### Corrigé
- Bug de concurrence sur `POST /api/presences` : deux appels simultanés avec le même étudiantId renvoyaient 500 au lieu de 409 (issue #23)

### Modifié
- RG6 abrogée : 1 relecteur → 2 relecteurs par exercice (enveloppe étape 3)
- `GET /api/exercices/{id}` : ajout du champ `noteProvisoire`
- Statut `PARTIELLEMENT_RELUE` ajouté au cycle de vie d'un exercice

### Retiré
- Issue #13 (rate-limiting) — non livrée, hors périmètre
- Issue #9 (correction de relecture après envoi) — non livrée, hors périmètre