# D2 — Modèle de données

**Objectif :** décrire les entités, leurs attributs et leurs cardinalités. Ce diagramme doit correspondre aux migrations Flyway de l'étape 2.

```mermaid
erDiagram
    PROMOTION ||--o{ ETUDIANT : "regroupe"
    PROMOTION ||--o{ SESSION : "concerne"
    SESSION ||--o{ PRESENCE : "enregistre"
    SESSION ||--o{ EXERCICE : "reçoit"
    ETUDIANT ||--o{ PRESENCE : "marque"
    ETUDIANT ||--o{ EXERCICE : "dépose"
        EXERCICE ||--o{ RELECTURE : "est relu par 2 pairs"

    PROMOTION {
        bigint id PK
        varchar nom "unique"
        timestamp cree_le
    }

    ETUDIANT {
        bigint id PK
        varchar nom
        varchar prenom
        bigint promotion_id FK
    }

    SESSION {
        bigint id PK
        varchar titre
        varchar code "unique, 6 caractères"
        bigint promotion_id FK
        timestamp ouverture_at
        timestamp expiration_at "ouverture + 15 min"
        timestamp cloture_at "null tant que non clôturée"
    }

    PRESENCE {
        bigint id PK
        bigint session_id FK
        bigint etudiant_id FK
        varchar source "ETUDIANT | FORMATEUR"
        timestamp marquee_at
    }

    EXERCICE {
        bigint id PK
        bigint session_id FK
        bigint etudiant_id FK
        varchar lien "URL"
        varchar statut "DEPOSE | EN_ATTENTE | RELUE | CLOTURE"
        timestamp depose_at
    }

        RELECTURE {
        bigint id PK
        bigint exercice_id FK "2 relectures par exercice, pas unique"
        bigint relecteur_id FK
        int note "0 à 20, entier"
        text commentaire
        varchar statut "EN_ATTENTE | RELUE"
        timestamp assignee_at
        timestamp rendue_at
    }
```

**Contraintes à implémenter dans les migrations Flyway :**

| Table | Contrainte | Règle |
|---|---|---|
| `PRESENCE` | `UNIQUE (session_id, etudiant_id)` | RG4 — une seule présence par session |
| `EXERCICE` | `UNIQUE (session_id, etudiant_id)` | RG5 — un seul exercice par session |
| `RELECTURE` | `UNIQUE (exercice_id)` | RG6 — un seul relecteur par exercice |
| `RELECTURE` | `CHECK (note BETWEEN 0 AND 20)` | RG3 — note entière 0-20 |
| `RELECTURE` | `CHECK (relecteur_id <> (SELECT etudiant_id FROM EXERCICE WHERE id = exercice_id))` | RG2 — pas d'auto-relecture (vérifié en service) |
| `SESSION` | `code` unique | RG1 — un code actif à la fois |
| `SESSION` | `expiration_at = ouverture_at + 15 min` | RG1 — calculé en service |

**Statuts des entités :**

- **`EXERCICE.statut`** : `DEPOSE` → `EN_ATTENTE` → `RELUE` → `CLOTURE`
  - `DEPOSE` : lien soumis, relecteur pas encore assigné
  - `EN_ATTENTE` : relecteur assigné, note pas encore rendue (Q11)
  - `RELUE` : note et commentaire rendus
  - `CLOTURE` : la session a été clôturée par le formateur
- **`RELECTURE.statut`** : `EN_ATTENTE` → `RELUE`
  - `EN_ATTENTE` : assignée, pas encore rendue
  - `RELUE` : note et commentaire rendus