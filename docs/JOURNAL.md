# Journal de bord — KF48-189

> Une entrée par étape, écrite au moment où elle est terminée.

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges complet (14 EF, 7 ENF, 16 RG, 10 sections), 4 diagrammes Mermaid (D1 cas d'usage, D2 modèle de données, D3 séquence présence, D4 états-transitions en bonus), 13 issues GitHub priorisées (9 Must, 3 Should, 1 Could), contrat OpenAPI complété (5 opérations imposées + 8 ajoutées). Jalon `[JALON] analyse` poussé.

**Bloqué :** ~15 min sur la contradiction Q10 / Q15 (note modifiable ou définitive). Tranchée en faveur de Q10 : Q11 décrit un usage concret (le formateur voit l'avancement des relectures, ce qui n'aurait aucun sens si tout était figé), Q15 n'est qu'une intention générale. Aussi ~10 min sur le trou du sujet : personne n'a précisé si un étudiant non présent peut déposer un exercice. Décision : oui, le dépôt est indépendant de la présence (Q12 prime).

**IA :** m'a proposé une première version du cahier des charges. J'ai vérifié chaque règle de gestion en la confrontant aux 16 questions de `CLIENT.md` : chaque RG cite sa source Qx. J'ai aussi fait relire les diagrammes Mermaid pour vérifier qu'ils correspondent bien aux codes HTTP du contrat (D3) et aux contraintes SQL (D2). J'ai reformulé 3 issues sur 13 parce que les titres proposés décrivaient des tâches techniques ("créer l'entité Relecture") au lieu de résultats utilisateur ("le relecteur rend sa note").

    ## Étape 2 — Première version (v0.1)

**Fait :** 9 issues Must livrées, chacune sur sa branche, sa PR et mergée sur `main`. Endpoints livrés : `POST /api/sessions`, `POST /api/presences`, `POST /api/exercices`, `POST /api/lectures/{id}`, `GET /api/tableau`, `GET /api/exercices/{id}`, `POST /api/sessions/{id}/cloture`. Jalon `[JALON] v0.1` poussé.

**Bloqué :** ~2 h sur la configuration Spring Boot 4.1.1 (Initializr ne propose plus 3.3.x, starters de test générés invalides). ~30 min sur un fichier `DeposerExerciceRequest.java` oublié qui cassait la compilation. ~20 min sur des fichiers backend (pom.xml, application.yml, migrations) restés hors du suivi Git : le `git add backend/src/main/java/` ne les prenait pas en compte. Corrigé par un commit de rattrapage.

**IA :** m'a proposé la structure de packages et le code des 9 issues. J'ai vérifié chaque endpoint en le confrontant au contrat `api/contrat.yaml` : chemins, verbes, codes HTTP, format d'erreur `{ code, message }`. Tests curl exécutés manuellement pour valider cas nominal + cas d'erreur de chaque endpoint.

## Étape 3 — Enveloppe

**Fait :** deux sujets séparés. (1) Bug de concurrence sur `POST /api/presences` reproduit par un test avant correction, puis corrigé par un handler global `DataIntegrityViolationException` → 409 au lieu de 500. (2) Changement de besoin : passage de 1 à 2 relecteurs par exercice, note finale = moyenne, note provisoire tant que les 2 ne sont pas rendues. Analyse mise à jour (CDC, D2, D4), migration V3 ajoutée, contrat API mis à jour.

**Bloqué :** ~30 min sur la reproduction du bug (il faut vraiment 2 threads simultanés, un test séquentiel ne reproduit pas). ~20 min sur la migration V3 : préserver les données existantes alors que la contrainte `UNIQUE(exercice_id)` est supprimée.

**IA :** m'a proposé le test de concurrence avec `CountDownLatch` et `AtomicInteger`. Vérifié en exécutant le test AVANT le correctif : il échoue bien (1 succès, 0 conflit, 1 erreur 500). Après le correctif, il passe (1 succès, 1 conflit, 0 erreur).

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :** l'issue #13 (rate-limiting, Could) et l'issue #9 (correction de relecture, Should) sont abandonnées. Le changement de besoin est un Must tardif ; les Could et Should sacrifiables passent en dernier.

**Note mise à jour (DoD) :** une issue n'est plus terminée tant que le test correspondant ne passe pas.