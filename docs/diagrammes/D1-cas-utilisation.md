# D1 — Diagramme de cas d'utilisation

**Objectif :** montrer les acteurs du système et ce que chacun peut faire.

```mermaid
graph TB
    subgraph Acteurs
        F[👨‍🏫 Formateur]
        E[👨‍🎓 Étudiant]
        R[👁️ Relecteur<br/>rôle d'étudiant]
    end

    subgraph "Système KFOKAM48"
        UC1[Ouvrir une session<br/>et obtenir un code]
        UC2[Clôturer une session]
        UC3[Ajouter une présence<br/>manuellement]
        UC4[Consulter le tableau<br/>de bord]
        
        UC5[Marquer sa présence<br/>avec un code]
        UC6[Déposer le lien<br/>de son exercice]
        UC7[Remplacer le lien<br/>de son exercice]
        UC8[Consulter sa note<br/>et le commentaire]
        
        UC9[Relire l'exercice<br/>d'un pair]
        UC10[Corriger sa relecture<br/>avant clôture]
    end

    F --> UC1
    F --> UC2
    F --> UC3
    F --> UC4

    E --> UC5
    E --> UC6
    E --> UC7
    E --> UC8

    R --> UC9
    R --> UC10

    %% Le relecteur est un étudiant assigné
    E -.->|devient| R
```

**Légende :**
- `F` = Formateur (acteur principal)
- `E` = Étudiant (acteur principal)
- `R` = Relecteur (rôle, pas un acteur distinct — voir section 2 du cahier des charges)
- La flèche pointillée `E -.-> R` indique qu'un étudiant devient relecteur par assignation automatique (RG7).