# Issue #12 — Relectures en attente visibles dans le tableau

**Statut :** couverte par l'issue #6.

## Où la fonctionnalité est livrée

| Élément | Fichier |
|---|---|
| Champ `relecturesEnAttente` dans le DTO | `backend/src/main/java/cm/kfokam48/backend/dto/response/TableauLigneResponse.java` |
| Requête SQL agrégée | `backend/src/main/java/cm/kfokam48/backend/repository/TableauRepository.java` |
| Exposé par l'API | `GET /api/tableau?promotionId=` |

## Requête SQL exacte

```sql
SELECT COUNT(*) FROM relecture r2
WHERE r2.relecteur_id = e.id AND r2.statut = 'EN_ATTENTE'