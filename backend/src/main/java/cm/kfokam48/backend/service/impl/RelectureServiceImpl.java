package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.RendreRelectureRequest;
import cm.kfokam48.backend.dto.response.RelectureResponse;
import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Relecture;
import cm.kfokam48.backend.exception.Exceptions.*;
import cm.kfokam48.backend.repository.ExerciceRepository;
import cm.kfokam48.backend.repository.RelectureRepository;
import cm.kfokam48.backend.service.RelectureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RelectureServiceImpl implements RelectureService {

    private final RelectureRepository relectureRepository;
    private final ExerciceRepository exerciceRepository;

    public RelectureServiceImpl(RelectureRepository relectureRepository,
                                ExerciceRepository exerciceRepository) {
        this.relectureRepository = relectureRepository;
        this.exerciceRepository = exerciceRepository;
    }

    @Override
    @Transactional
    public RelectureResponse rendre(Long relectureId, RendreRelectureRequest request) {

        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> new RelectureInconnueException(relectureId));

        if (request.note() == null || request.note() < 0 || request.note() > 20) {
            throw new NoteInvalideException();
        }

        Exercice exercice = relecture.getExercice();
        if (exercice.getEtudiant().getId().equals(relecture.getRelecteur().getId())) {
            throw new AutoRelectureException();
        }

        boolean sessionCloturee = exercice.getSession().getClotureAt() != null;
        boolean dejaRendue = relecture.getStatut() == Relecture.StatutRelecture.RELUE;

        if (dejaRendue && sessionCloturee) {
            throw new RelectureDejaRendueException();
        }

        relecture.setNote(request.note());
        relecture.setCommentaire(request.commentaire());
        relecture.setStatut(Relecture.StatutRelecture.RELUE);
        relecture.setRendueAt(LocalDateTime.now());
        Relecture saved = relectureRepository.save(relecture);

        // Recalculer le statut de l'exercice (RG17, RG18)
        long rendues = relectureRepository.countByExerciceIdAndStatut(
                exercice.getId(), Relecture.StatutRelecture.RELUE);

        if (rendues >= 2) {
            exercice.setStatut(Exercice.StatutExercice.RELUE);
        } else if (rendues == 1) {
            exercice.setStatut(Exercice.StatutExercice.PARTIELLEMENT_RELUE);
        }
        exerciceRepository.save(exercice);

        return new RelectureResponse(
                saved.getId(),
                exercice.getId(),
                saved.getNote(),
                saved.getCommentaire(),
                saved.getStatut().name(),
                saved.getRendueAt()
        );
    }
}