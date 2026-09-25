package cm.kfokam48.backend.repository;

import cm.kfokam48.backend.entity.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    List<Relecture> findAllByExerciceId(Long exerciceId);

    long countByExerciceIdAndStatut(Long exerciceId, Relecture.StatutRelecture statut);

    List<Relecture> findAllByRelecteurIdAndStatut(
            Long relecteurId, Relecture.StatutRelecture statut);

    long countByRelecteurIdAndStatut(
            Long relecteurId, Relecture.StatutRelecture statut);
}