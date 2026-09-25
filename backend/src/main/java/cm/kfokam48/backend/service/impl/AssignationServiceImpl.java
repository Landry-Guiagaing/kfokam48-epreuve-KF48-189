package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.entity.*;
import cm.kfokam48.backend.repository.ExerciceRepository;
import cm.kfokam48.backend.repository.PresenceRepository;
import cm.kfokam48.backend.repository.RelectureRepository;
import cm.kfokam48.backend.service.AssignationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssignationServiceImpl implements AssignationService {

    private static final int NB_RELECTEURS = 2;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public AssignationServiceImpl(PresenceRepository presenceRepository,
                                  ExerciceRepository exerciceRepository,
                                  RelectureRepository relectureRepository) {
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Override
    @Transactional
    public Optional<Relecture> assigner(Exercice exercice) {

        Long sessionId = exercice.getSession().getId();
        Long auteurId = exercice.getEtudiant().getId();

        // Deja assigne ?
        List<Relecture> existantes = relectureRepository.findAllByExerciceId(exercice.getId());
        if (existantes.size() >= NB_RELECTEURS) {
            return Optional.of(existantes.get(0));
        }

        List<Long> dejaAssignes = existantes.stream()
                .map(r -> r.getRelecteur().getId())
                .toList();

        // Candidats : presents ET ayant depose, hors auteur, hors deja assignes
        List<Long> presents = presenceRepository.findAllBySessionId(sessionId)
                .stream().map(p -> p.getEtudiant().getId()).toList();
        List<Long> ayantDepose = exerciceRepository.findAllBySessionId(sessionId)
                .stream().map(e -> e.getEtudiant().getId()).toList();

        List<Long> candidats = presents.stream()
                .filter(ayantDepose::contains)
                .filter(id -> !id.equals(auteurId))
                .filter(id -> !dejaAssignes.contains(id))
                .distinct()
                .toList();

        if (candidats.isEmpty()) {
            exercice.setStatut(Exercice.StatutExercice.EN_ATTENTE);
            exerciceRepository.save(exercice);
            return Optional.empty();
        }

        // Tirer les relecteurs manquants au hasard
        List<Long> pool = new ArrayList<>(candidats);
        int manquants = NB_RELECTEURS - existantes.size();
        Relecture derniere = null;

        for (int i = 0; i < manquants && !pool.isEmpty(); i++) {
            Long id = pool.remove(RANDOM.nextInt(pool.size()));
            Etudiant e = new Etudiant();
            e.setId(id);

            Relecture r = new Relecture();
            r.setExercice(exercice);
            r.setRelecteur(e);
            r.setStatut(Relecture.StatutRelecture.EN_ATTENTE);
            derniere = relectureRepository.save(r);
        }

        exercice.setStatut(Exercice.StatutExercice.EN_ATTENTE);
        exerciceRepository.save(exercice);

        return Optional.ofNullable(derniere);
    }
}