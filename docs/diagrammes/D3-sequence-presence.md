# D3 — Séquence : « marquer sa présence »

**Objectif :** décrire le cas nominal et au moins deux cas d'erreur. Ce diagramme doit correspondre aux codes HTTP du contrat `api/contrat.yaml`.

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant F as Frontend<br/>(React)
    participant C as PresenceController
    participant S as PresenceService
    participant R as PresenceRepository
    participant DB as Base de données

    E->>F: saisit le code + sélectionne son nom
    F->>C: POST /api/presences<br/>{ code, etudiantId }
    
    C->>C: validation des entrées<br/>(@Valid, code non vide, etudiantId présent)
    
    alt entrée invalide
        C-->>F: 400 { code: "VALIDATION_ERREUR",<br/>message: "champ manquant" }
        F-->>E: affiche l'erreur
    else entrée valide
        C->>S: enregistrerPresence(code, etudiantId)
        
        S->>R: findByCode(code)
        R->>DB: SELECT * FROM session WHERE code = ?
        
        alt code inconnu
            DB-->>R: null
            R-->>S: Optional.empty()
            S-->>C: CodeInconnuException
            C-->>F: 400 { code: "CODE_INCONNU",<br/>message: "Le code saisi est inconnu." }
            F-->>E: affiche l'erreur
        else code trouvé
            DB-->>R: Session
            R-->>S: Session
            
            S->>S: vérifie RG1 :<br/>now < expirationAt ?
            
            alt code expiré (RG1)
                S-->>C: CodeExpireException
                C-->>F: 410 { code: "CODE_EXPIRE",<br/>message: "Le code de présence a expiré." }
                F-->>E: affiche l'erreur
            else code non expiré
                S->>S: vérifie RG15 :<br/>session non clôturée ?
                
                alt session clôturée
                    S-->>C: SessionClotureeException
                    C-->>F: 400 { code: "SESSION_CLOTUREE",<br/>message: "La session est clôturée." }
                    F-->>E: affiche l'erreur
                else session ouverte
                    S->>R: existsBySessionIdAndEtudiantId(sessionId, etudiantId)
                    R->>DB: SELECT COUNT(*) FROM presence WHERE ...
                    
                    alt déjà présent (RG4)
                        DB-->>R: true
                        R-->>S: true
                        S-->>C: DejaPresentException
                        C-->>F: 409 { code: "DEJA_PRESENT",<br/>message: "Vous avez déjà marqué votre présence." }
                        F-->>E: affiche l'erreur
                    else pas encore présent
                        DB-->>R: false
                        R-->>S: false
                        S->>R: save(Presence)
                        R->>DB: INSERT INTO presence (...)
                        DB-->>R: Presence
                        R-->>S: Presence
                        S-->>C: Presence (source = ETUDIANT)
                        C-->>F: 201 { id, sessionId,<br/>etudiantId, source: "ETUDIANT" }
                        F-->>E: affiche la confirmation
                    end
                end
            end
        end
    end
```

**Correspondance avec le contrat :**

| Cas | Code HTTP | Code erreur |
|---|---|---|
| Succès | `201` | — |
| Entrée invalide | `400` | `VALIDATION_ERREUR` |
| Code inconnu | `400` | `CODE_INCONNU` |
| Code expiré | `410` | `CODE_EXPIRE` |
| Déjà présent | `409` | `DEJA_PRESENT` |
| Session clôturée | `400` | `SESSION_CLOTUREE` |

**Règles de gestion couvertes :**
- RG1 : expiration 15 min
- RG4 : unicité de la présence par session
- RG15 : pas de présence après clôture