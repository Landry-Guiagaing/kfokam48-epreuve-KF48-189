package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.entity.Etudiant;
import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Presence;
import cm.kfokam48.backend.entity.Relecture;
import cm.kfokam48.backend.repository.ExerciceRepository;
import cm.kfokam48.backend.repository.PresenceRepository;
import cm.kfokam48.backend.repository.RelectureRepository;
import cm.kfokam48.backend.service.AssignationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class AssignationServiceImpl implements AssignationService {

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

        // RG6 : un seul relecteur par exercice
        if (relectureRepository.existsByExerciceId(exercice.getId())) {
            return relectureRepository.findByExerciceId(exercice.getId());
        }

        Long sessionId = exercice.getSession().getId();
        Long auteurId = exercice.getEtudiant().getId();

        // RG7 : candidats = étudiants présents ET ayant déposé un exercice
        //        hors l'auteur (RG2)
        List<Long> presents = presenceRepository.findAllBySessionId(sessionId)
                .stream()
                .map(p -> p.getEtudiant().getId())
                .toList();

        List<Long> ayantDepose = exerciceRepository.findAllBySessionId(sessionId)
                .stream()
                .map(e -> e.getEtudiant().getId())
                .toList();

        List<Etudiant> candidats = presents.stream()
                .filter(ayantDepose::contains)
                .filter(id -> !id.equals(auteurId))
                .distinct()
                .map(id -> {
                    Etudiant e = new Etudiant();
                    e.setId(id);
                    return e;
                })
                .toList();

        if (candidats.isEmpty()) {
            // Aucun relecteur disponible → l'exercice reste EN_ATTENTE
            exercice.setStatut(Exercice.StatutExercice.EN_ATTENTE);
            exerciceRepository.save(exercice);
            return Optional.empty();
        }

        // Tirage au hasard
        Etudiant relecteur = candidats.get(RANDOM.nextInt(candidats.size()));

        Relecture relecture = new Relecture();
        relecture.setExercice(exercice);
        relecture.setRelecteur(relecteur);
        relecture.setStatut(Relecture.StatutRelecture.EN_ATTENTE);

        Relecture saved = relectureRepository.save(relecture);

        // L'exercice bascule en EN_ATTENTE dès qu'un relecteur est assigné (Q11)
        exercice.setStatut(Exercice.StatutExercice.EN_ATTENTE);
        exerciceRepository.save(exercice);

        return Optional.of(saved);
    }
}