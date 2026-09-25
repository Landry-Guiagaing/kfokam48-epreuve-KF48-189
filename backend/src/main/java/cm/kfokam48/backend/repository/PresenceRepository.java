package cm.kfokam48.backend.repository;

import cm.kfokam48.backend.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    long countByEtudiantIdAndSessionId(Long etudiantId, Long sessionId);

    List<Presence> findAllBySessionId(Long sessionId);
}