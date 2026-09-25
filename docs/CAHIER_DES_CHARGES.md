# Cahier des charges — KFOKAM48 Présence & Relecture

**Auteur :** Guiagaing Landry · KF48-189
**Version :** 1 · **Date :** 2026-09-25
**Frontend choisi :** React, parce que c'est le framework que je maîtrise le mieux, l'écosystème est mature, et Vite permet un démarrage rapide sans configuration lourde.

---

## 1. Contexte et objectif

La direction de KFOKAM48 gère des sessions de cours en présentiel et des exercices rendus par les étudiants. Aujourd'hui, le suivi se fait manuellement : feuille de présence papier, liens d'exercices éparpillés dans des conversations, notes transmises oralement. Le formateur n'a **aucune vue consolidée** de qui était présent, qui a rendu, qui doit relire, et quelle est la moyenne d'un étudiant.

L'objectif est de fournir une application web qui permet à un formateur d'**ouvrir une session avec un code de présence temporaire**, aux étudiants de **marquer leur présence** avec ce code, de **déposer le lien de leur exercice**, et de **relire l'exercice d'un pair** (note + commentaire). Le formateur dispose d'un **tableau de bord** consolidant présence, dépôts et moyennes par étudiant.

Le périmètre fonctionnel couvre : gestion des sessions, des présences, des exercices, des relectures et du tableau récapitulatif.

---

## 2. Acteurs et rôles

**Décision structurante :** le **relecteur n'est pas un acteur distinct**. C'est un **étudiant dans un état particulier** (« assigné à une relecture »). Conséquence sur le modèle de données : une seule entité `Etudiant`, et une entité `Relecture` qui porte l'assignation.

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| **Formateur** | • Ouvrir une session (titre + promotion) et obtenir un code<br>• Clôturer une session<br>• Ajouter une présence manuellement (source `FORMATEUR`)<br>• Consulter le tableau de bord d'une promotion | • Marquer une présence à la place d'un étudiant sans que ça se voie<br>• Relire un exercice à la place d'un étudiant<br>• Déposer un exercice (il n'en a pas) |
| **Étudiant** | • Marquer sa présence avec un code valide<br>• Déposer / remplacer le lien de son exercice<br>• Relire l'exercice d'un pair (note 0–20 + commentaire)<br>• Consulter sa propre note et le commentaire reçus<br>• Corriger sa relecture tant que la session n'est pas clôturée | • Relire son propre exercice (RG2)<br>• Voir le nom de son relecteur (Q8)<br>• Marquer sa présence deux fois pour une même session<br>• Déposer deux exercices pour une même session<br>• Modifier une relecture après clôture de la session |
| **Relecteur** *(rôle, pas acteur)* | • Un étudiant assigné à la relecture d'un exercice donné, avec les mêmes droits qu'un étudiant sur cette relecture précise | • Choisir qui il relit (assignation automatique, Q7)<br>• Relire plusieurs exercices (Q6 : un seul par exercice) |

---

## 3. Périmètre

**Inclus dans cette version :**
- Gestion des promotions (lecture seule, données de démo préchargées)
- Gestion des étudiants (lecture seule, données de démo préchargées)
- Ouverture et clôture d'une session par le formateur
- Code de présence à 6 caractères, expiration 15 minutes (RG1)
- Marquage de présence par code, source `ETUDIANT`
- Ajout manuel de présence par le formateur, source `FORMATEUR` (Q14)
- Dépôt d'exercice (lien URL) par un étudiant pour une session
- Remplacement du lien tant qu'aucune relecture n'a commencé (Q13)
- Assignation automatique d'un relecteur parmi les étudiants présents (Q7)
- Saisie d'une relecture : note entière 0–20 + commentaire (Q9)
- Modification d'une relecture tant que la session n'est pas clôturée (Q10)
- Tableau de bord formateur par promotion (Q16)

**Explicitement exclu :**
- Authentification / mots de passe (Q1)
- Gestion CRUD des promotions et des étudiants (données préchargées)
- Envoi d'emails, notifications push, SMS
- Upload de fichiers (seul un **lien** est stocké, pas le fichier)
- Historique versionné des relectures (une seule note active)
- Interface mobile native (web responsive seulement, pas d'app store)
- Gestion des rôles avancée (admin, super-admin…)
- Export CSV / PDF du tableau
- Anti-triche autre que le rate-limiting Q4 (5 erreurs → blocage 2 minutes)

---

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| **EF1** | Le formateur ouvre une session et obtient un code | Quand je crée une session avec un titre et une promotion, alors je reçois un code à 6 caractères et une date d'expiration à +15 min | Must |
| **EF2** | Le formateur clôture une session | Quand je clôture une session, alors aucune nouvelle présence ni nouveau dépôt n'est accepté pour cette session | Must |
| **EF3** | L'étudiant marque sa présence avec un code | Quand je saisis un code valide et non expiré, alors ma présence apparaît dans le tableau du formateur avec source `ETUDIANT` | Must |
| **EF4** | Le formateur ajoute une présence manuellement | Quand j'ajoute une présence à la main, alors elle apparaît avec source `FORMATEUR` dans le tableau | Should |
| **EF5** | L'étudiant dépose le lien de son exercice | Quand je dépose un lien valide pour une session ouverte, alors l'exercice passe au statut `DEPOSE` | Must |
| **EF6** | L'étudiant remplace le lien de son exercice | Quand je remplace le lien et qu'aucune relecture n'a commencé, alors le nouveau lien remplace l'ancien | Should |
| **EF7** | Le système assigne un relecteur à chaque exercice déposé | Quand un exercice est déposé, alors un relecteur est tiré au hasard parmi les étudiants présents à la session (hors lui-même) | Must |
| **EF8** | Le relecteur rend sa note et son commentaire | Quand je saisis une note entière entre 0 et 20 et un commentaire, alors la relecture passe au statut `RELUE` | Must |
| **EF9** | Le relecteur corrige sa relecture | Quand je corrige ma note et que la session n'est pas clôturée, alors la nouvelle note remplace l'ancienne | Should |
| **EF10** | L'étudiant relu consulte sa note | Quand je consulte mon exercice, alors je vois ma note et le commentaire, mais jamais le nom du relecteur | Must |
| **EF11** | Le formateur consulte son tableau de bord | Quand je demande le tableau d'une promotion, alors je vois par étudiant : présences, exercices déposés, moyenne, relectures en attente | Must |
| **EF12** | Le relecteur ne peut pas relire son propre exercice | Quand je tente de relire mon propre exercice, alors je reçois une erreur 403 `AUTO_RELECTURE` | Must |
| **EF13** | Le code de présence est rate-limité | Quand je me trompe 5 fois de code en moins de 2 minutes, alors je suis bloqué 2 minutes | Should |
| **EF14** | L'exercice en attente est visible dans le tableau | Quand un relecteur n'a pas rendu sa relecture, alors la colonne `relecturesEnAttente` du tableau l'indique | Must |
| **EF15** | Chaque exercice est relu par deux pairs | Quand un exercice est déposé, alors deux relecteurs distincts sont assignés | Must |
| **EF16** | La note finale est la moyenne des deux notes | Quand les deux relectures sont rendues, alors la note finale est la moyenne (arrondie à l'entier) | Must |
| **EF17** | Si un seul relecteur a rendu, la note est provisoire | Quand une seule relecture est rendue, alors la note affichée est celle du relecteur, marquée `provisoire: true` | Must |

---

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| **ENF1** | L'interface de marquage de présence est utilisable sur un téléphone | Test manuel sur une fenêtre de 375px de large ; le formulaire de saisie du code est accessible sans zoom |
| **ENF2** | Le tableau du formateur répond en moins de 2 secondes pour une promotion de 60 étudiants | Chronométrage sur les données de démonstration (60 étudiants) |
| **ENF3** | Le code de présence est difficile à deviner | Alphabet de 6 caractères parmi un jeu d'au moins 32 symboles (≈ 10⁹ combinaisons), + rate-limiting Q4 |
| **ENF4** | Les erreurs API respectent le format imposé | Toute réponse d'erreur contient `{ code, message }`, jamais de stack trace |
| **ENF5** | Le schéma de base de données est versionné | Toutes les évolutions passent par des migrations Flyway, jamais par `ddl-auto=update` |
| **ENF6** | L'application démarre depuis un clone vierge | Test sur une machine vierge en suivant uniquement le README |
| **ENF7** | Les données de démonstration sont chargées au démarrage | Une promotion, 60 étudiants, 3 sessions, quelques exercices et relectures préchargés |

---

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| **RG1** | Un code de présence expire 15 minutes après l'ouverture de la session | Q2 |
| **RG2** | Un étudiant ne peut pas relire son propre exercice | Q5 |
| **RG3** | Une note est un entier compris entre 0 et 20 | Q9 |
| **RG4** | Un étudiant ne peut marquer qu'une seule présence par session | Q16 (implicite) |
| **RG6** | Un exercice est relu par **deux** étudiants distincts, hors auteur | Q6 modifié par enveloppe étape 3 |
| **RG6** | Un exercice n'a qu'un seul relecteur | Q6 |
| **RG7** | Le relecteur est tiré au hasard parmi les étudiants présents à la session, hors l'auteur | Q7 |
| **RG8** | L'étudiant relu ne voit jamais le nom de son relecteur | Q8 |
| **RG9** | Une relecture peut être corrigée tant que la session n'est pas clôturée | Q10 |
| **RG10** | Un exercice dont la relecture n'est pas rendue reste au statut `EN_ATTENTE` et apparaît comme tel dans le tableau | Q11 |
| **RG11** | Un exercice peut être déposé jusqu'à la clôture de la session | Q12 |
| **RG12** | Le lien d'un exercice peut être remplacé tant qu'aucune relecture n'a commencé | Q13 |
| **RG13** | Une présence ajoutée par le formateur porte la source `FORMATEUR` et est distinguable d'une présence `ETUDIANT` | Q14 |
| **RG14** | Après 5 erreurs de code en moins de 2 minutes, l'étudiant est bloqué 2 minutes | Q4 |
| **RG15** | Une présence ne peut pas être marquée après la clôture de la session | Q3 + Q12 |
| **RG16** | Le tableau du formateur présente, par étudiant : présences, exercices déposés, moyenne des notes reçues, relectures en attente | Q16 |
| **RG17** | La note finale est la moyenne des deux notes, arrondie à l'entier le plus proche | Enveloppe étape 3 |
| **RG18** | Tant que les deux relectures ne sont pas rendues, la note affichée est provisoire | Enveloppe étape 3 |

---

## 7. Zones d'ombre, hypothèses et contradictions

### Contradictions tranchées

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| **Q10** (« le relecteur peut corriger sa note tant que la session n'est pas clôturée ») vs **Q15** (« la note est définitive une fois envoyée ») | **Q10 gagne** : la note est modifiable tant que la session n'est pas clôturée | Q10 décrit un usage concret et daté (fenêtre de correction claire), Q15 exprime une intention générale (« c'est plus honnête »). Le besoin métier réel est de pouvoir corriger une erreur de saisie avant la clôture. Q11 mentionne d'ailleurs que le formateur voit l'état d'avancement des relectures, ce qui n'aurait aucun sens si tout était figé immédiatement. |
| **Q3** (« pas de présence après la fin de session ») vs **Q12** (« dépôt possible jusqu'à clôture ») | **Pas une contradiction** : ce sont deux objets différents. Q3 concerne la **présence** (liée au code, qui expire à +15 min), Q12 concerne le **dépôt d'exercice** (indépendant du code). Les deux règles coexistent. | Objets distincts, sémantiques distinctes. |

### Le trou que personne n'a vu

Le client ne précise **nulle part** :
1. Si un étudiant **non présent** peut déposer un exercice (Q12 autorise le dépôt jusqu'à clôture, mais Q7 dit que le relecteur est choisi « parmi les présents »).
2. Ce qui se passe si **aucun étudiant présent** ne peut être relecteur (session avec un seul étudiant, ou tous les présents sont l'auteur).

### Hypothèses retenues

| Point | Décision retenue | Pourquoi |
|---|---|---|
| Dépôt par un étudiant non présent | **Autorisé** : le dépôt est indépendant de la présence. Q12 prime. | Q12 parle explicitement de « ceux qui n'ont pas de connexion le soir même » — donc des étudiants qui n'étaient pas là au moment de la session. Le dépôt est un acte asynchrone. |
| Choix du relecteur | **Parmi les étudiants présents ET ayant eux-mêmes déposé un exercice**, hors l'auteur. | Sinon on ne garantit pas la réciprocité : un étudiant qui relit sans avoir rien déposé n'a aucune raison de faire l'effort. Cela reste cohérent avec Q7 (« parmi les présents »). |
| Aucun relecteur disponible | L'exercice reste au statut **`EN_ATTENTE`** et apparaît comme tel dans le tableau (Q11). Le formateur pourra le voir et agir manuellement plus tard. | Q11 décrit exactement ce cas : « L'exercice reste en attente et je dois le voir clairement dans mon tableau. » |
| Session avec un seul étudiant présent | L'étudiant peut déposer son exercice, mais **aucune relecture n'est assignée** (RG2 : interdiction de se relire soi-même). L'exercice reste `EN_ATTENTE`. | Conséquence directe de RG2 et Q7. |
| Statut d'un exercice déposé sans relecture assignée | Reste `DEPOSE` puis bascule en `EN_ATTENTE` tant qu'aucun relecteur n'est trouvé. | Cohérent avec Q11. |

### Changement de besoin (enveloppe étape 3)

| Point | Décision | Pourquoi |
|---|---|---|
| Nombre de relecteurs par exercice | **2** au lieu de 1 | Enveloppe : un seul relecteur ne suffit pas, l'étudiant peut ne jamais avoir de note |
| Note finale | Moyenne des 2 notes, arrondie à l'entier | Enveloppe : « la note retenue est la moyenne des deux » |
| Note provisoire | Affichée si une seule relecture rendue, marquée `provisoire: true` | Enveloppe : « si un seul des deux a rendu, on affiche sa note en attendant, mais marquée comme provisoire » |
| RG6 initiale | **Abrogée** par le changement | Enveloppe étape 3 |

---

## 8. Contraintes techniques

**Backend :**
- **B1** — Java 17+, Maven, wrapper `mvnw` commité
- **B2** — Contrat `api/contrat.yaml` respecté à la lettre : chemins, verbes, codes de statut, format d'erreur `{ code, message }`
- **B3** — Séparation stricte contrôleur / service / repository ; aucune entité JPA exposée en JSON (DTO uniquement)
- **B4** — Validation des entrées + gestion centralisée des erreurs via `@RestControllerAdvice`
- **B5** — Schéma versionné par **Flyway**, `ddl-auto=validate` en runtime, `ddl-auto=create-drop` uniquement en test
- **B6** — Un test unitaire sur une règle métier réelle (ex : RG1 expiration du code) + un test d'intégration sur un endpoint (ex : `POST /api/presences`)

**Frontend :**
- **F1** — React (Vite), build qui passe
- **F2** — Trois écrans : formateur, étudiant, relecteur
- **F3** — Couche API dédiée (`src/api/`), pas de `fetch` dispersé, états de chargement/erreur gérés, aucune règle métier dupliquée

**Base de données :**
- **H2 en mémoire** pour le développement et les tests (démarrage sans dépendance externe)
- **Migrations Flyway** versionnées dans `backend/src/main/resources/db/migration/`
- **Données de démonstration** chargées au démarrage via une migration Flyway dédiée (`V2__seed_demo.sql`)

**Démarrage :**
- Trois commandes maximum, documentées dans le README :
  1. `cd backend && ./mvnw spring-boot:run`
  2. `cd frontend && npm install && npm run dev`
  3. Ouvrir `http://localhost:5173`

---

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` (ce document)
- `docs/JOURNAL.md` — journal de bord, une entrée par étape
- `docs/diagrammes/D1-cas-utilisation.md`
- `docs/diagrammes/D2-modele-donnees.md`
- `docs/diagrammes/D3-sequence-presence.md`
- `docs/diagrammes/D4-etats-exercice.md` *(bonus)*
- `api/contrat.yaml` — contrat OpenAPI complété
- `backend/` — application Spring Boot fonctionnelle
- `frontend/` — application React fonctionnelle
- `README.md` — instructions d'installation testées
- `CHANGELOG.md` — cohérent avec l'historique Git
- Issues GitHub — backlog priorisé
- `SOUMISSION.md` — fichier de soumission

---

## 10. Démarche prévue

**Étape 1 — Analyse et conception**
Rédaction du cahier des charges, des 4 diagrammes Mermaid, du contrat OpenAPI complété, création des issues GitHub. Aucun code. Jalon `[JALON] analyse`.

**Étape 2 — Première version**
Construction des stories Must uniquement : sessions, présences, exercices, relectures, tableau. Une branche par issue, une PR par branche. Jalon `[JALON] v0.1`.

**Étape 3 — Enveloppe**
Réception de l'enveloppe au près du surveillant, ouverture d'une issue dédiée, reproduction du bug, mise à jour du contrat et des migrations, mise à jour du cahier des charges et des diagrammes concernés.

**Étape 4 — Version finale**
Finalisation des stories Should restantes, `CHANGELOG.md`, `README.md` testé depuis un clone vierge, tri du backlog. Jalon `[JALON] v1.0`.

**Étape 5 — Soumission**
Rédaction de `SOUMISSION.md`, vérification des liens en navigation privée, téléversement sur la plateforme avant 18h00.

**En cas de retard :** priorité absolue aux Must, puis au README fonctionnel, puis au CHANGELOG. Le Should et le Could sont sacrifiables.

**Definition of Done — une issue est terminée quand :**
- Le code est sur une branche dédiée, mergée sur `main` via une PR
- La PR cite `Closes #<numéro>` et ferme l'issue automatiquement
- Les tests passent (`./mvnw test` côté backend, `npm run build` côté frontend)
- Le code respecte le contrat `api/contrat.yaml` (codes HTTP, format d'erreur)
- Le message de commit cite la règle de gestion concernée (ex : `RG1`)
- Aucun fichier généré n'est commité (`target/`, `node_modules/`, `dist/`)

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 2026-09-25 | Version initiale, rédigée à l'étape 1 |