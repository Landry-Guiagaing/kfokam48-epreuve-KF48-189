package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.request.MarquerPresenceRequest;
import cm.kfokam48.backend.entity.Promotion;
import cm.kfokam48.backend.entity.Session;
import cm.kfokam48.backend.entity.Etudiant;
import cm.kfokam48.backend.exception.Exceptions.DejaPresentException;
import cm.kfokam48.backend.repository.EtudiantRepository;
import cm.kfokam48.backend.repository.PresenceRepository;
import cm.kfokam48.backend.repository.PromotionRepository;
import cm.kfokam48.backend.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PresenceConcurrenceTest {

    @Autowired private PresenceService presenceService;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private PresenceRepository presenceRepository;

    private String code;
    private Long etudiantId;

    @BeforeEach
    void setup() {
        Promotion promotion = new Promotion();
        promotion.setNom("PROMO-TEST-" + System.nanoTime());
        promotionRepository.save(promotion);

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Test");
        etudiant.setPrenom("Concurrence");
        etudiant.setPromotion(promotion);
        etudiantRepository.save(etudiant);
        this.etudiantId = etudiant.getId();

        Session session = new Session();
        session.setTitre("Session test concurrence");
        session.setCode("TEST" + (System.nanoTime() % 100));
        session.setPromotion(promotion);
        session.setOuvertureAt(LocalDateTime.now());
        session.setExpirationAt(LocalDateTime.now().plusMinutes(15));
        sessionRepository.save(session);
        this.code = session.getCode();
    }

    @Test
    void deuxAppelsSimultanesMemeEtudiant_doiventProduireUneSeulePresenceEtUn409() throws Exception {

        int nbThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        CountDownLatch startGate = new CountDownLatch(1);

        AtomicInteger succes = new AtomicInteger(0);
        AtomicInteger conflits = new AtomicInteger(0);
        AtomicInteger erreurs = new AtomicInteger(0);

        Callable<Void> tache = () -> {
            startGate.await(); // synchronisation : tous les threads partent en même temps
            try {
                presenceService.marquer(new MarquerPresenceRequest(code, etudiantId));
                succes.incrementAndGet();
            } catch (DejaPresentException e) {
                conflits.incrementAndGet();
            } catch (Exception e) {
                erreurs.incrementAndGet();
                e.printStackTrace();
            }
            return null;
        };

        Future<Void> f1 = executor.submit(tache);
        Future<Void> f2 = executor.submit(tache);

        startGate.countDown(); // top départ

        f1.get(10, TimeUnit.SECONDS);
        f2.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        long presencesEnBase = presenceRepository.count();

        System.out.println(">>> succes=" + succes.get()
                + " conflits=" + conflits.get()
                + " erreurs=" + erreurs.get()
                + " presencesEnBase=" + presencesEnBase);

        // Une seule présence doit être en base
        assertThat(presencesEnBase).isEqualTo(1);
        // Un seul succès
        assertThat(succes.get()).isEqualTo(1);
        // Un seul 409
        assertThat(conflits.get()).isEqualTo(1);
        // Aucune 500 (le bug actuel)
        assertThat(erreurs.get()).isZero();
    }
}