package cm.kfokam48.backend.repository;

import cm.kfokam48.backend.dto.response.TableauLigneResponse;
import cm.kfokam48.backend.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TableauRepository extends JpaRepository<Etudiant, Long> {

    /**
     * Tableau agrégé par étudiant d'une promotion (EF11, RG16).
     *
     * Colonnes :
     *  - etudiant_id
     *  - nom complet (prenom + nom)
     *  - nombre de présences
     *  - nombre d'exercices déposés
     *  - moyenne des notes reçues (NULL si aucune note)
     *  - nombre de relectures en attente (assignées à l'étudiant, non rendues)
     */
    @Query(value = """
        SELECT
            e.id AS etudiantId,
            CONCAT(e.prenom, ' ', e.nom) AS nom,
            (SELECT COUNT(*) FROM presence p WHERE p.etudiant_id = e.id) AS presences,
            (SELECT COUNT(*) FROM exercice ex WHERE ex.etudiant_id = e.id) AS exercicesDeposes,
            (SELECT AVG(CAST(r.note AS DOUBLE))
                FROM relecture r
                JOIN exercice ex2 ON ex2.id = r.exercice_id
                WHERE ex2.etudiant_id = e.id AND r.note IS NOT NULL) AS moyenne,
            (SELECT COUNT(*) FROM relecture r2
                WHERE r2.relecteur_id = e.id AND r2.statut = 'EN_ATTENTE') AS relecturesEnAttente
        FROM etudiant e
        WHERE e.promotion_id = :promotionId
        ORDER BY e.nom, e.prenom
        """, nativeQuery = true)
    List<Object[]> findTableauByPromotion(@Param("promotionId") Long promotionId);
}