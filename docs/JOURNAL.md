# Journal de bord — KF48-189

> Une entrée par étape, écrite au moment où elle est terminée.

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges complet (14 EF, 7 ENF, 16 RG, 10 sections), 4 diagrammes Mermaid (D1 cas d'usage, D2 modèle de données, D3 séquence présence, D4 états-transitions en bonus), 13 issues GitHub priorisées (9 Must, 3 Should, 1 Could), contrat OpenAPI complété (5 opérations imposées + 8 ajoutées). Jalon `[JALON] analyse` poussé.

**Bloqué :** ~15 min sur la contradiction Q10 / Q15 (note modifiable ou définitive). Tranchée en faveur de Q10 : Q11 décrit un usage concret (le formateur voit l'avancement des relectures, ce qui n'aurait aucun sens si tout était figé), Q15 n'est qu'une intention générale. Aussi ~10 min sur le trou du sujet : personne n'a précisé si un étudiant non présent peut déposer un exercice. Décision : oui, le dépôt est indépendant de la présence (Q12 prime).

**IA :** m'a proposé une première version du cahier des charges. J'ai vérifié chaque règle de gestion en la confrontant aux 16 questions de `CLIENT.md` : chaque RG cite sa source Qx. J'ai aussi fait relire les diagrammes Mermaid pour vérifier qu'ils correspondent bien aux codes HTTP du contrat (D3) et aux contraintes SQL (D2). J'ai reformulé 3 issues sur 13 parce que les titres proposés décrivaient des tâches techniques ("créer l'entité Relecture") au lieu de résultats utilisateur ("le relecteur rend sa note").