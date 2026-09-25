# KFOKAM48 — Présence & Relecture

Application de gestion des présences en session de cours et de relecture d'exercices par les pairs.

**Frontend choisi : React (Vite), parce que c'est le framework le plus mature pour une SPA légère, avec un écosystème vaste et un démarrage instantané via Vite.**

## Stack

- **Backend** : Java 17, Spring Boot 4.1.1, Maven, Flyway, H2
- **Frontend** : React 18, Vite, Axios
- **Base de données** : H2 en mémoire (dev), PostgreSQL en production

## Démarrage rapide

### Option 1 — Docker (recommandé)

```bash
docker compose up --build
```

- Frontend : http://localhost:5173
- Backend : http://localhost:8080
- Console H2 : http://localhost:8080/h2-console (JDBC : `jdbc:h2:mem:kfokam48`, user `sa`, password vide)

### Option 2 — Manuel (2 terminaux)

**Terminal 1 — Backend :**

```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 — Frontend :**

```bash
cd frontend
npm install
npm run dev
```

Ouvrir : http://localhost:5173

## Données de démonstration

Au démarrage, Flyway charge automatiquement :
- 1 promotion (`KF48-2026`)
- 12 étudiants
- 1 session ouverte (code à 6 caractères, valide 15 min)
- 7 présences (6 `ETUDIANT` + 1 `FORMATEUR`)
- 4 exercices déposés
- 4 relectures assignées

## Endpoints principaux

| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/sessions` | Ouvrir une session |
| POST | `/api/sessions/{id}/cloture` | Clôturer une session |
| POST | `/api/presences` | Marquer sa présence |
| POST | `/api/exercices` | Déposer un exercice |
| GET | `/api/exercices/{id}` | Consulter sa note |
| POST | `/api/lectures/{id}` | Rendre une relecture |
| GET | `/api/tableau?promotionId=` | Tableau du formateur |

Contrat complet : [`api/contrat.yaml`](api/contrat.yaml)

## Format d'erreur

Toutes les erreurs respectent le format imposé :

```json
{ "code": "CODE_EXPIRE", "message": "Le code de présence a expiré." }
```

## Tests

```bash
cd backend
./mvnw test
```

Couvre : RG1 (expiration), RG4 (unicité), RG17/RG18 (double relecture), bug de concurrence (issue #23).

## Structure

```
.
├── api/                 contrat OpenAPI
├── backend/             Spring Boot
│   ├── src/main/java/cm/kfokam48/backend/
│   │   ├── controller/
│   │   ├── service/impl/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/   (V1, V2, V3)
├── frontend/            React + Vite
├── docs/                cahier des charges, journal, diagrammes
└── docker-compose.yml
```

## Documentation

- [Cahier des charges](docs/CAHIER_DES_CHARGES.md)
- [Journal de bord](docs/JOURNAL.md)
- [Diagrammes](docs/diagrammes/)
- [Changelog](CHANGELOG.md)