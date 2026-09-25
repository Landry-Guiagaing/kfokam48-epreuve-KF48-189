package cm.kfokam48.backend.repository;

import cm.kfokam48.backend.entity.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    Optional<Exercice> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Exercice> findAllBySessionId(Long sessionId);

    List<Exercice> findAllByEtudiantId(Long etudiantId);
}