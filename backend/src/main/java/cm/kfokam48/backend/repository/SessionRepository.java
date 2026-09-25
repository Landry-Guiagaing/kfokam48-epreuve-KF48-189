package cm.kfokam48.backend.repository;

import cm.kfokam48.backend.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByCode(String code);
    boolean existsByCode(String code);
}