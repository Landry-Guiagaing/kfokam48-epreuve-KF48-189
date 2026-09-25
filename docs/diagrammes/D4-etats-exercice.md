# D4 — États-transitions : cycle de vie d'un exercice *(bonus)*

**Objectif :** décrire les états d'un exercice et les transitions possibles, en cohérence avec Q11, Q12, Q13, Q10.

```mermaid
stateDiagram-v2
    [*] --> DEPOSE : L'étudiant dépose<br/>le lien de son exercice

    DEPOSE --> EN_ATTENTE : Un relecteur est<br/>assigné (RG7)
    DEPOSE --> DEPOSE : L'étudiant remplace<br/>le lien (RG12)
    DEPOSE --> CLOTURE : Le formateur<br/>clôture la session

    EN_ATTENTE --> RELUE : Le relecteur rend<br/>sa note + commentaire (EF8)
    EN_ATTENTE --> EN_ATTENTE : Le relecteur corrige<br/>sa note (RG9)
    EN_ATTENTE --> CLOTURE : Le formateur<br/>clôture la session

    RELUE --> RELUE : Le relecteur corrige<br/>sa note (RG9)
    RELUE --> CLOTURE : Le formateur<br/>clôture la session

    CLOTURE --> [*] : État final

    note right of DEPOSE
        Aucun relecteur assigné
        (ex: session avec 1 seul présent)
    end note

    note right of EN_ATTENTE
        Visible dans le tableau
        comme "relecture en attente"
        (Q11)
    end note

    note right of RELUE
        Note et commentaire
        consultables par l'auteur
        (sans le nom du relecteur, Q8)
    end note

    note right of CLOTURE
        Aucune modification
        possible (Q15, RG9)
    end note
```

**Transitions autorisées :**

| De | Vers | Déclencheur | Règle |
|---|---|---|---|
| `[*]` | `DEPOSE` | Dépôt du lien | EF5, RG11 |
| `DEPOSE` | `EN_ATTENTE` | Assignation d'un relecteur | RG7, EF7 |
| `DEPOSE` | `DEPOSE` | Remplacement du lien | RG12, EF6 |
| `EN_ATTENTE` | `RELUE` | Relecture rendue | EF8 |
| `EN_ATTENTE` | `EN_ATTENTE` | Correction de la note | RG9, EF9 |
| `RELUE` | `RELUE` | Correction de la note | RG9, EF9 |
| `*` (sauf `CLOTURE`) | `CLOTURE` | Clôture de la session | EF2, RG15 |
| `CLOTURE` | `[*]` | État final | — |

**États interdits :**
- Pas de retour de `CLOTURE` vers un autre état (RG15)
- Pas de passage direct de `DEPOSE` à `RELUE` sans assignation de relecteur
- Pas de passage de `RELUE` à `DEPOSE` (Q13 : remplacement impossible une fois la relecture commencée)