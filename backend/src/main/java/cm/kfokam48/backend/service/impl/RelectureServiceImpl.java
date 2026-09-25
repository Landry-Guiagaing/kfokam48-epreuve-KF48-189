package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.RendreRelectureRequest;
import cm.kfokam48.backend.dto.response.RelectureResponse;
import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Relecture;
import cm.kfokam48.backend.exception.Exceptions.AutoRelectureException;
import cm.kfokam48.backend.exception.Exceptions.NoteInvalideException;
import cm.kfokam48.backend.exception.Exceptions.RelectureDejaRendueException;
import cm.kfokam48.backend.exception.Exceptions.RelectureInconnueException;
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

        // 1) Charger la relecture
        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> new RelectureInconnueException(relectureId));

        // 2) Vérifier que la note est bien un entier entre 0 et 20 (RG3)
        if (request.note() == null
                || request.note() < 0
                || request.note() > 20) {
            throw new NoteInvalideException();
        }

        // 3) Vérifier l'auto-relecture (RG2)
        Exercice exercice = relecture.getExercice();
        if (exercice.getEtudiant().getId().equals(relecture.getRelecteur().getId())) {
            throw new AutoRelectureException();
        }

        // 4) Vérifier la session (RG9) : correction possible tant que non clôturée
        boolean sessionCloturee = exercice.getSession().getClotureAt() != null;

        // 5) Vérifier si déjà rendue ET session clôturée (RG9 + RG15)
        boolean dejaRendue = relecture.getStatut() == Relecture.StatutRelecture.RELUE;

        if (dejaRendue && sessionCloturee) {
            throw new RelectureDejaRendueException();
        }

        // 6) Mettre à jour la relecture
        relecture.setNote(request.note());
        relecture.setCommentaire(request.commentaire());
        relecture.setStatut(Relecture.StatutRelecture.RELUE);
        relecture.setRendueAt(LocalDateTime.now());

        Relecture saved = relectureRepository.save(relecture);

        // 7) Mettre à jour le statut de l'exercice
        exercice.setStatut(Exercice.StatutExercice.RELUE);
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